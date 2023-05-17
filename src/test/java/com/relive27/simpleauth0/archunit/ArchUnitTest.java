package com.relive27.simpleauth0.archunit;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * @author: ReLive27
 * @date: 2023/5/17 17:32
 */
@AnalyzeClasses(packages = "com.relive27.simpleauth0", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchUnitTest {

    /**
     * 检查是否所有 Controller 类都具有后缀“Controller”
     */
    @ArchTest
    static ArchRule controllers_suffixed_should_be =
            classes().that().resideInAPackage("..controller..")
                    .or().areAnnotatedWith(RestController.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .allowEmptyShould(true);

    /**
     * 检查实体类是否位于 "entity" 包下
     */
    @ArchTest
    static final ArchRule tablename_must_reside_in_a_entity_package =
            classes().that().areAnnotatedWith(TableName.class)
                    .should().resideInAPackage("..entity..")
                    .as("TableName should reside in a package '..entity..'")
                    .allowEmptyShould(true);

    /**
     * 检查配置类是否位于 "config" 包中
     */
    @ArchTest
    static final ArchRule configs_must_reside_in_a_config_package =
            classes().that().areAnnotatedWith(Configuration.class)
                    .or().areNotNestedClasses()
                    .and().areAnnotatedWith(ConfigurationProperties.class)
                    .should().resideInAPackage("..config..")
                    .as("Configs should reside in a package '..config..'")
                    .allowEmptyShould(true);


    /**
     * 图层检查，Service层仅被Controller访问，Dao层仅被Service层访问
     */
    @ArchTest
    static ArchRule layer_inspection = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Dao").definedBy("..dao..")

            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Dao").mayOnlyBeAccessedByLayers("Service")
            .allowEmptyShould(true);
}
