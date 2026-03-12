<template>
  <div class="identity-create-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left">
        <el-button type="text" icon="el-icon-arrow-left" @click="cancel">
          返回列表
        </el-button>
        <h1 class="page-title">新建企业身份源</h1>
      </div>
      <div class="header-right">
        <el-button type="text" icon="el-icon-question">
          查看文档
        </el-button>
      </div>
    </div>

    <!-- 步骤向导 -->
    <div class="step-wizard">
      <div
        v-for="(step, index) in steps"
        :key="index"
        :class="['step-item', { active: currentStep === index, completed: currentStep > index }]"
        @click="goToStep(index)"
      >
        <div class="step-number">
          <i v-if="currentStep > index" class="el-icon-check" />
          <span v-else>{{ index + 1 }}</span>
        </div>
        <div class="step-info">
          <div class="step-title">{{ step.title }}</div>
          <div class="step-desc">{{ step.desc }}</div>
        </div>
        <div v-if="index < steps.length - 1" class="step-line" />
      </div>
    </div>

    <!-- 步骤内容 -->
    <div class="step-content">
      <!-- 步骤1：选择类型 -->
      <div v-if="currentStep === 0" class="step-panel">
        <div class="step-header">
          <h2>选择身份源类型</h2>
          <p>选择您要连接的企业身份提供商类型</p>
        </div>

        <div class="type-grid">
          <div
            v-for="type in identityTypes"
            :key="type.value"
            :class="['type-card', { active: form.type === type.value }]"
            @click="selectType(type.value)"
          >
            <div class="type-icon" :class="type.value">
              <i :class="type.icon" />
            </div>
            <div class="type-content">
              <h3>{{ type.label }}</h3>
              <p>{{ type.description }}</p>
              <div class="type-tags">
                <el-tag v-for="tag in type.tags" :key="tag" size="mini">{{ tag }}</el-tag>
              </div>
            </div>
            <div v-if="form.type === type.value" class="selected-indicator">
              <i class="el-icon-check" />
            </div>
          </div>
        </div>

        <div class="type-compare">
          <el-link type="primary" @click="showCompare = true">
            <i class="el-icon-info" /> 不知道如何选择？查看身份源对比
          </el-link>
        </div>
      </div>

      <!-- 步骤2：基础配置 -->
      <div v-if="currentStep === 1" class="step-panel">
        <div class="step-header">
          <h2>基础配置</h2>
          <p>配置身份源的基本信息</p>
        </div>

        <el-card class="config-card" shadow="never">
          <div class="config-section">
            <h3 class="section-title">
              <i class="el-icon-info" />
              基本信息
            </h3>
            <el-form
              ref="basicForm"
              :model="form"
              :rules="basicRules"
              label-width="120px"
              label-position="right"
              class="compact-form"
            >
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="身份源名称" prop="name">
                    <el-input
                      v-model="form.name"
                      placeholder="例如：公司主LDAP"
                      maxlength="50"
                      show-word-limit
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="标识" prop="code">
                    <el-input
                      v-model="form.code"
                      placeholder="唯一标识，如 company-ldap"
                      maxlength="30"
                    >
                      <template slot="prepend">idp_</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="描述" prop="desc" class="full-width-item">
                <el-input
                  v-model="form.desc"
                  type="textarea"
                  :rows="2"
                  placeholder="描述该身份源的用途，方便其他管理员理解"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="状态" class="switch-item">
                <el-switch
                  v-model="form.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
                <span class="form-tip-text">创建后立即启用该身份源</span>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </div>

      <!-- 步骤3：连接配置 -->
      <div v-if="currentStep === 2" class="step-panel">
        <div class="step-header">
          <h2>连接配置</h2>
          <p>配置与身份源的连接参数</p>
        </div>

        <el-card class="config-card" shadow="never">
          <!-- LDAP/AD 配置 -->
          <template v-if="form.type === 'ldap' || form.type === 'ad'">
            <div class="config-section">
              <h3 class="section-title">
                <i class="el-icon-s-platform" />
                服务器配置
              </h3>

              <el-form
                ref="ldapForm"
                :model="form"
                :rules="ldapRules"
                label-width="120px"
                label-position="right"
                class="compact-form"
              >
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="服务器地址" prop="server">
                      <el-input
                        v-model="form.server"
                        placeholder="ldap://ldap.example.com"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="端口" prop="port">
                      <el-input-number v-model="form.port" :min="1" :max="65535" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="连接方式">
                  <el-radio-group v-model="form.connectionType" size="small">
                    <el-radio-button label="plain">普通连接</el-radio-button>
                    <el-radio-button label="ssl">SSL/TLS</el-radio-button>
                    <el-radio-button label="starttls">STARTTLS</el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="Base DN" prop="baseDN">
                      <el-input v-model="form.baseDN" placeholder="dc=example,dc=com" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="用户过滤器">
                      <el-input v-model="form.userFilter" placeholder="(objectClass=person)" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </div>

            <div class="config-section">
              <h3 class="section-title">
                <i class="el-icon-user-solid" />
                认证配置
              </h3>

              <el-form
                :model="form"
                label-width="120px"
                label-position="right"
                class="compact-form"
              >
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="管理员 DN" prop="bindDN">
                      <el-input v-model="form.bindDN" placeholder="cn=admin,dc=example,dc=com" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="管理员密码" prop="password">
                      <el-input v-model="form.password" show-password placeholder="输入管理员密码" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item class="toggle-advanced-item">
                  <el-button type="text" @click="showAdvanced = !showAdvanced">
                    <i :class="showAdvanced ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" />
                    {{ showAdvanced ? '收起' : '展开' }}高级配置
                  </el-button>
                </el-form-item>

                <template v-if="showAdvanced">
                  <el-row :gutter="24">
                    <el-col :span="12">
                      <el-form-item label="连接超时">
                        <el-input-number v-model="form.timeout" :min="1" :max="60" />
                        <span class="unit">秒</span>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="同步频率">
                        <el-select v-model="form.syncInterval" style="width: 100%">
                          <el-option label="手动同步" value="manual" />
                          <el-option label="每小时" value="1h" />
                          <el-option label="每6小时" value="6h" />
                          <el-option label="每12小时" value="12h" />
                          <el-option label="每天" value="24h" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </template>
              </el-form>
            </div>
          </template>

          <!-- OIDC 配置 -->
          <template v-if="form.type === 'oidc'">
            <div class="config-section">
              <h3 class="section-title">
                <i class="el-icon-link" />
                OIDC 配置
              </h3>

              <el-form
                ref="oidcForm"
                :model="form"
                :rules="oidcRules"
                label-width="120px"
                label-position="right"
                class="compact-form"
              >
                <el-form-item label="Issuer URL" prop="issuer">
                  <el-input v-model="form.issuer" placeholder="https://idp.example.com" />
                </el-form-item>

                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="Client ID" prop="clientId">
                      <el-input v-model="form.clientId" placeholder="输入Client ID" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="Client Secret" prop="clientSecret">
                      <el-input v-model="form.clientSecret" show-password placeholder="输入Client Secret" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="Scope">
                  <el-checkbox-group v-model="form.scopes" size="small">
                    <el-checkbox-button label="openid">openid</el-checkbox-button>
                    <el-checkbox-button label="profile">profile</el-checkbox-button>
                    <el-checkbox-button label="email">email</el-checkbox-button>
                    <el-checkbox-button label="phone">phone</el-checkbox-button>
                    <el-checkbox-button label="address">address</el-checkbox-button>
                  </el-checkbox-group>
                </el-form-item>

                <el-form-item label="回调地址" class="copy-field-item">
                  <el-input :value="callbackUrl" readonly>
                    <el-button slot="append" icon="el-icon-document-copy" @click="copyCallback">复制</el-button>
                  </el-input>
                  <span class="form-tip-text">请在您的IdP中将此地址配置为允许的回调地址</span>
                </el-form-item>
              </el-form>
            </div>
          </template>

          <!-- SAML 配置 -->
          <template v-if="form.type === 'saml'">
            <div class="config-section">
              <h3 class="section-title">
                <i class="el-icon-key" />
                SAML 配置
              </h3>

              <el-form
                ref="samlForm"
                :model="form"
                :rules="samlRules"
                label-width="120px"
                label-position="right"
                class="compact-form"
              >
                <el-form-item label="配置方式">
                  <el-radio-group v-model="form.samlConfigType" size="small">
                    <el-radio-button label="url">Metadata URL</el-radio-button>
                    <el-radio-button label="manual">手动配置</el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <template v-if="form.samlConfigType === 'url'">
                  <el-form-item label="Metadata URL" prop="metadataUrl">
                    <el-input v-model="form.metadataUrl" placeholder="https://idp.example.com/metadata" />
                  </el-form-item>
                </template>

                <template v-else>
                  <el-form-item label="Entity ID" prop="entityId">
                    <el-input v-model="form.entityId" placeholder="输入Entity ID" />
                  </el-form-item>

                  <el-form-item label="SSO URL" prop="ssoUrl">
                    <el-input v-model="form.ssoUrl" placeholder="https://idp.example.com/sso" />
                  </el-form-item>

                  <el-form-item label="证书" prop="cert">
                    <el-input
                      v-model="form.cert"
                      type="textarea"
                      :rows="5"
                      placeholder="-----BEGIN CERTIFICATE-----"
                    />
                  </el-form-item>
                </template>

                <div class="config-section-sp"></div>

                <el-form-item label="SP Entity ID" class="copy-field-item">
                  <el-input :value="spEntityId" readonly>
                    <el-button slot="append" icon="el-icon-document-copy" @click="copyEntityId">复制</el-button>
                  </el-input>
                </el-form-item>

                <el-form-item label="ACS URL" class="copy-field-item">
                  <el-input :value="acsUrl" readonly>
                    <el-button slot="append" icon="el-icon-document-copy" @click="copyAcsUrl">复制</el-button>
                  </el-input>
                </el-form-item>
              </el-form>
            </div>
          </template>

          <!-- 连接测试 -->
          <div class="connection-test">
            <el-button
              type="warning"
              :loading="testing"
              :icon="testStatus.icon"
              @click="testConnection"
            >
              {{ testStatus.text }}
            </el-button>
            <span v-if="testStatus.message" :class="['test-message', testStatus.type]">
              {{ testStatus.message }}
            </span>
          </div>
        </el-card>
      </div>

      <!-- 步骤4：属性映射 -->
      <div v-if="currentStep === 3" class="step-panel">
        <div class="step-header">
          <h2>属性映射</h2>
          <p>配置身份源返回的用户属性与本系统的映射关系</p>
        </div>

        <el-card class="config-card" shadow="never">
          <div class="mapping-header">
            <span>属性映射列表</span>
            <el-button type="text" icon="el-icon-refresh" @click="resetMapping">恢复默认</el-button>
          </div>

          <el-table :data="attributeMapping" class="mapping-table">
            <el-table-column label="本地属性" width="180">
              <template slot-scope="{ row }">
                <el-tag size="small">{{ row.local }}</el-tag>
                <div class="attr-desc">{{ row.desc }}</div>
              </template>
            </el-table-column>
            <el-table-column label="身份源属性" min-width="200">
              <template slot-scope="{ row, $index }">
                <el-input v-model="row.remote" placeholder="输入属性名">
                  <template v-if="getTypeDefaultAttr(form.type, row.local)" slot="append">
                    <el-tooltip content="使用默认值">
                      <el-button icon="el-icon-refresh-left" @click="row.remote = getTypeDefaultAttr(form.type, row.local)" />
                    </el-tooltip>
                  </template>
                </el-input>
              </template>
            </el-table-column>
            <el-table-column label="必填" width="80" align="center">
              <template slot-scope="{ row }">
                <el-checkbox v-model="row.required" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="{ $index }">
                <el-button type="text" class="danger" @click="removeMapping($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="add-mapping">
            <el-button type="text" icon="el-icon-plus" @click="addMapping">添加自定义映射</el-button>
          </div>

          <el-divider />

          <div class="preview-section">
            <h4>数据预览</h4>
            <el-alert type="info" :closable="false">
              <div slot="title">
                配置完成后，系统将尝试从身份源获取用户数据并应用上述映射。
                <el-link type="primary">查看示例数据</el-link>
              </div>
            </el-alert>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="step-actions">
      <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
      <el-button v-if="currentStep < steps.length - 1" type="primary" @click="nextStep">
        下一步
      </el-button>
      <el-button v-else type="primary" :loading="saving" @click="save">
        保存并启用
      </el-button>
      <el-button @click="cancel">取消</el-button>
    </div>

    <!-- 类型对比弹窗 -->
    <el-dialog title="身份源类型对比" :visible.sync="showCompare" width="800px">
      <el-table :data="compareData" style="width: 100%">
        <el-table-column prop="feature" label="特性" width="150" />
        <el-table-column prop="ldap" label="LDAP/AD">
          <template slot-scope="{ row }">
            <i v-if="row.ldap === true" class="el-icon-check check-icon" />
            <i v-else-if="row.ldap === false" class="el-icon-close close-icon" />
            <span v-else>{{ row.ldap }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="oidc" label="OIDC">
          <template slot-scope="{ row }">
            <i v-if="row.oidc === true" class="el-icon-check check-icon" />
            <i v-else-if="row.oidc === false" class="el-icon-close close-icon" />
            <span v-else>{{ row.oidc }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="saml" label="SAML">
          <template slot-scope="{ row }">
            <i v-if="row.saml === true" class="el-icon-check check-icon" />
            <i v-else-if="row.saml === false" class="el-icon-close close-icon" />
            <span v-else>{{ row.saml }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "IdentitySourceCreate",

  data() {
    return {
      currentStep: 0,
      steps: [
        { title: "选择类型", desc: "选择身份源协议" },
        { title: "基础配置", desc: "配置基本信息" },
        { title: "连接配置", desc: "配置连接参数" },
        { title: "属性映射", desc: "配置字段映射" }
      ],
      showCompare: false,
      showAdvanced: false,
      testing: false,
      testStatus: {
        icon: "",
        text: "测试连接",
        message: "",
        type: ""
      },
      saving: false,

      identityTypes: [
        {
          label: "LDAP",
          value: "ldap",
          icon: "el-icon-s-cooperation",
          description: "轻量级目录访问协议，适用于OpenLDAP等标准LDAP服务器",
          tags: ["标准协议", "广泛使用"]
        },
        {
          label: "Active Directory",
          value: "ad",
          icon: "el-icon-s-platform",
          description: "微软Windows域服务，适用于企业Windows环境",
          tags: ["微软", "企业首选"]
        },
        {
          label: "OIDC / OAuth2",
          value: "oidc",
          icon: "el-icon-link",
          description: "OpenID Connect，现代身份认证标准，支持社交登录",
          tags: ["现代标准", "云原生"]
        },
        {
          label: "SAML 2.0",
          value: "saml",
          icon: "el-icon-key",
          description: "安全断言标记语言，适用于企业SSO场景",
          tags: ["企业SSO", "成熟标准"]
        }
      ],

      compareData: [
        { feature: "单点登录(SSO)", ldap: true, oidc: true, saml: true },
        { feature: "自动用户发现", ldap: true, oidc: false, saml: false },
        { feature: "组/角色同步", ldap: true, oidc: "需额外配置", saml: "需额外配置" },
        { feature: "多因素认证", ldap: false, oidc: true, saml: true },
        { feature: "社交登录支持", ldap: false, oidc: true, saml: false },
        { feature: "配置复杂度", ldap: "中等", oidc: "简单", saml: "复杂" }
      ],

      form: {
        type: "",
        name: "",
        code: "",
        desc: "",
        enabled: true,

        // LDAP/AD
        server: "",
        port: 389,
        connectionType: "plain",
        baseDN: "",
        userFilter: "(objectClass=person)",
        bindDN: "",
        password: "",
        timeout: 10,
        syncInterval: "24h",

        // OIDC
        issuer: "",
        clientId: "",
        clientSecret: "",
        scopes: ["openid", "profile", "email"],

        // SAML
        samlConfigType: "url",
        metadataUrl: "",
        entityId: "",
        ssoUrl: "",
        cert: ""
      },

      basicRules: {
        name: [
          { required: true, message: "请输入身份源名称", trigger: "blur" },
          { min: 2, max: 50, message: "长度在2-50个字符", trigger: "blur" }
        ],
        code: [
          { required: true, message: "请输入标识", trigger: "blur" },
          { pattern: /^[a-z0-9-]+$/, message: "只能包含小写字母、数字和连字符", trigger: "blur" }
        ]
      },

      ldapRules: {
        server: [{ required: true, message: "请输入服务器地址", trigger: "blur" }],
        port: [{ required: true, message: "请输入端口", trigger: "blur" }],
        baseDN: [{ required: true, message: "请输入Base DN", trigger: "blur" }]
      },

      oidcRules: {
        issuer: [{ required: true, message: "请输入Issuer URL", trigger: "blur" }],
        clientId: [{ required: true, message: "请输入Client ID", trigger: "blur" }],
        clientSecret: [{ required: true, message: "请输入Client Secret", trigger: "blur" }]
      },

      samlRules: {
        metadataUrl: [{ required: true, message: "请输入Metadata URL", trigger: "blur" }],
        entityId: [{ required: true, message: "请输入Entity ID", trigger: "blur" }],
        ssoUrl: [{ required: true, message: "请输入SSO URL", trigger: "blur" }],
        cert: [{ required: true, message: "请输入证书", trigger: "blur" }]
      },

      attributeMapping: [
        { local: "username", desc: "用户名", remote: "", required: true },
        { local: "email", desc: "邮箱", remote: "", required: true },
        { local: "nickname", desc: "昵称", remote: "", required: false },
        { local: "phone", desc: "手机号", remote: "", required: false }
      ]
    };
  },

  computed: {
    callbackUrl() {
      return `${window.location.origin}/api/auth/callback/${this.form.code || "{code}"}`;
    },
    spEntityId() {
      return `${window.location.origin}/api/saml/metadata/${this.form.code || "{code}"}`;
    },
    acsUrl() {
      return `${window.location.origin}/api/saml/acs/${this.form.code || "{code}"}`;
    }
  },

  watch: {
    "form.type": {
      immediate: true,
      handler(type) {
        if (type) {
          this.initDefaultMapping(type);
        }
      }
    }
  },

  methods: {
    selectType(type) {
      this.form.type = type;
      // 设置默认端口
      if (type === "ldap" || type === "ad") {
        this.form.port = type === "ad" ? 636 : 389;
      }
    },

    goToStep(index) {
      if (index < this.currentStep) {
        this.currentStep = index;
      }
    },

    nextStep() {
      if (this.currentStep === 0 && !this.form.type) {
        this.$message.warning("请选择身份源类型");
        return;
      }

      if (this.currentStep === 1) {
        this.$refs.basicForm.validate(valid => {
          if (valid) this.currentStep++;
        });
        return;
      }

      if (this.currentStep === 2) {
        const formRef = this.getFormRef();
        if (formRef) {
          this.$refs[formRef].validate(valid => {
            if (valid) this.currentStep++;
          });
          return;
        }
      }

      if (this.currentStep < this.steps.length - 1) {
        this.currentStep++;
      }
    },

    prevStep() {
      if (this.currentStep > 0) {
        this.currentStep--;
      }
    },

    getFormRef() {
      const map = {
        ldap: "ldapForm",
        ad: "ldapForm",
        oidc: "oidcForm",
        saml: "samlForm"
      };
      return map[this.form.type];
    },

    testConnection() {
      this.testing = true;
      this.testStatus = { icon: "el-icon-loading", text: "测试中...", message: "", type: "" };

      setTimeout(() => {
        this.testing = false;
        const success = Math.random() > 0.3;
        if (success) {
          this.testStatus = {
            icon: "el-icon-success",
            text: "重新测试",
            message: "连接成功！成功获取到服务器响应",
            type: "success"
          };
        } else {
          this.testStatus = {
            icon: "el-icon-refresh-right",
            text: "重新测试",
            message: "连接失败：无法连接到服务器，请检查地址和端口",
            type: "error"
          };
        }
      }, 2000);
    },

    getTypeDefaultAttr(type, attr) {
      const defaults = {
        ldap: {
          username: "uid",
          email: "mail",
          nickname: "cn",
          phone: "telephoneNumber"
        },
        ad: {
          username: "sAMAccountName",
          email: "mail",
          nickname: "displayName",
          phone: "telephoneNumber"
        },
        oidc: {
          username: "sub",
          email: "email",
          nickname: "name",
          phone: "phone_number"
        },
        saml: {
          username: "NameID",
          email: "email",
          nickname: "displayName",
          phone: "telephone"
        }
      };
      return defaults[type]?.[attr] || "";
    },

    initDefaultMapping(type) {
      this.attributeMapping.forEach(item => {
        item.remote = this.getTypeDefaultAttr(type, item.local);
      });
    },

    resetMapping() {
      this.initDefaultMapping(this.form.type);
    },

    addMapping() {
      this.attributeMapping.push({
        local: "",
        desc: "",
        remote: "",
        required: false
      });
    },

    removeMapping(index) {
      this.attributeMapping.splice(index, 1);
    },

    copyCallback() {
      this.copyToClipboard(this.callbackUrl);
    },

    copyEntityId() {
      this.copyToClipboard(this.spEntityId);
    },

    copyAcsUrl() {
      this.copyToClipboard(this.acsUrl);
    },

    copyToClipboard(text) {
      const input = document.createElement("input");
      input.value = text;
      document.body.appendChild(input);
      input.select();
      document.execCommand("copy");
      document.body.removeChild(input);
      this.$message.success("已复制到剪贴板");
    },

    save() {
      this.saving = true;
      setTimeout(() => {
        this.saving = false;
        this.$message.success(`身份源 "${this.form.name}" 创建成功`);
        this.$router.push("/identity/enterprise");
      }, 1500);
    },

    cancel() {
      this.$confirm("确定取消创建？已填写的信息将不会保存。", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "继续编辑",
        type: "warning"
      }).then(() => {
        this.$router.push("/identity/enterprise");
      }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.identity-create-page {
  padding: 24px 32px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .page-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
      }
    }
  }

  .step-wizard {
    display: flex;
    justify-content: center;
    margin-bottom: 32px;
    padding: 0 40px;

    .step-item {
      display: flex;
      align-items: center;
      flex: 1;
      max-width: 280px;
      cursor: pointer;
      position: relative;

      &.active {
        .step-number {
          background: #1890ff;
          color: #fff;
          border-color: #1890ff;
        }
        .step-title {
          color: #1890ff;
          font-weight: 600;
        }
      }

      &.completed {
        .step-number {
          background: #52c41a;
          color: #fff;
          border-color: #52c41a;
        }
      }

      .step-number {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        border: 2px solid #d9d9d9;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        margin-right: 12px;
        background: #fff;
        color: #999;
        flex-shrink: 0;
      }

      .step-info {
        .step-title {
          font-size: 14px;
          color: #333;
        }
        .step-desc {
          font-size: 12px;
          color: #999;
          margin-top: 2px;
        }
      }

      .step-line {
        flex: 1;
        height: 2px;
        background: #e8e8e8;
        margin: 0 16px;
        min-width: 40px;
      }
    }
  }

  .step-content {
    max-width: 900px;
    margin: 0 auto;

    .step-panel {
      .step-header {
        text-align: center;
        margin-bottom: 24px;

        h2 {
          font-size: 18px;
          font-weight: 600;
          margin: 0 0 8px 0;
        }

        p {
          color: #666;
          margin: 0;
        }
      }
    }
  }

  .type-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    .type-card {
      background: #fff;
      border: 2px solid #e8e8e8;
      border-radius: 12px;
      padding: 24px;
      cursor: pointer;
      display: flex;
      align-items: flex-start;
      gap: 16px;
      transition: all 0.3s;
      position: relative;

      &:hover {
        border-color: #1890ff;
        box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
      }

      &.active {
        border-color: #1890ff;
        background: #e6f7ff;

        .selected-indicator {
          position: absolute;
          top: 12px;
          right: 12px;
          width: 24px;
          height: 24px;
          background: #1890ff;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 14px;
        }
      }

      .type-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        flex-shrink: 0;

        &.ldap {
          background: #fff2e8;
          color: #fa8c16;
        }
        &.ad {
          background: #e6f7ff;
          color: #1890ff;
        }
        &.oidc {
          background: #f6ffed;
          color: #52c41a;
        }
        &.saml {
          background: #f9f0ff;
          color: #722ed1;
        }
      }

      .type-content {
        flex: 1;

        h3 {
          font-size: 16px;
          font-weight: 600;
          margin: 0 0 8px 0;
        }

        p {
          font-size: 13px;
          color: #666;
          margin: 0 0 12px 0;
          line-height: 1.5;
        }

        .type-tags {
          .el-tag {
            margin-right: 8px;
          }
        }
      }
    }
  }

  .type-compare {
    text-align: center;
  }

  .config-card {
    border-radius: 12px;
    border: 1px solid #e8e8e8;

    ::v-deep .el-card__body {
      padding: 28px 32px;
    }

    .config-section {
      &:not(:last-child) {
        margin-bottom: 32px;
        padding-bottom: 32px;
        border-bottom: 1px solid #f0f0f0;
      }

      .section-title {
        font-size: 15px;
        font-weight: 600;
        margin: 0 0 20px 0;
        display: flex;
        align-items: center;
        gap: 8px;
        color: #262626;

        i {
          color: #1890ff;
          font-size: 16px;
        }
      }
    }

    .compact-form {
      .el-form-item {
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .el-form-item__label {
          font-size: 14px;
          color: #595959;
          font-weight: 500;
          padding-right: 16px;
        }

        .el-form-item__content {
          line-height: 32px;
        }
      }

      .full-width-item {
        .el-input,
        .el-textarea {
          width: 100%;
        }
      }

      .switch-item {
        .el-form-item__content {
          display: flex;
          align-items: center;
          gap: 12px;
        }
      }
    }

    .form-tip {
      font-size: 12px;
      color: #8c8c8c;
      margin-top: 4px;
      line-height: 1.5;
    }

    .form-tip-text {
      font-size: 13px;
      color: #8c8c8c;
      margin-left: 8px;
    }

    .toggle-advanced-item {
      margin-top: 8px;

      .el-button--text {
        font-size: 13px;
        color: #595959;

        i {
          margin-right: 4px;
        }

        &:hover {
          color: #1890ff;
        }
      }
    }

    .copy-field-item {
      .el-input-group__append {
        background: #fff;
        padding: 0 12px;

        .el-button {
          color: #1890ff;
        }
      }
    }

    .config-section-sp {
      height: 16px;
      border-top: 1px solid #f0f0f0;
      margin: 20px 0;
    }

    .unit {
      margin-left: 8px;
      color: #595959;
      font-size: 13px;
    }
  }

  .connection-test {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px dashed #e8e8e8;
    display: flex;
    align-items: center;
    gap: 16px;

    .test-message {
      font-size: 13px;

      &.success {
        color: #52c41a;
      }
      &.error {
        color: #ff4d4f;
      }
    }
  }

  .mapping-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-weight: 600;
  }

  .mapping-table {
    .attr-desc {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
  }

  .add-mapping {
    margin-top: 16px;
  }

  .preview-section {
    margin-top: 24px;

    h4 {
      font-size: 14px;
      margin: 0 0 12px 0;
    }
  }

  .step-actions {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    border-top: 1px solid #e8e8e8;
    padding: 16px 32px;
    display: flex;
    justify-content: center;
    gap: 12px;
    z-index: 100;

    .el-button {
      min-width: 100px;
    }
  }

  .check-icon {
    color: #52c41a;
    font-size: 16px;
    font-weight: bold;
  }

  .close-icon {
    color: #ff4d4f;
    font-size: 16px;
    font-weight: bold;
  }

  .danger {
    color: #ff4d4f;
  }
}

@media (max-width: 768px) {
  .identity-create-page {
    padding: 16px;

    .type-grid {
      grid-template-columns: 1fr;
    }

    .step-wizard {
      flex-direction: column;
      gap: 16px;

      .step-item {
        max-width: none;

        .step-line {
          display: none;
        }
      }
    }
  }
}
</style>
