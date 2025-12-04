<template>
  <div class="identity-create-page">

    <!-- 顶部标题 -->
    <h2 class="page-title">新增企业身份源</h2>
    <p class="page-desc">选择身份源类型并进行相关配置。</p>

    <div class="create-body">

      <!-- 左侧类型选择 -->
      <div class="type-select">
        <div
          v-for="item in types"
          :key="item.value"
          :class="['type-item', form.type === item.value ? 'active' : '']"
          @click="selectType(item.value)"
        >
          <img :src="item.icon" />
          <span>{{ item.label }}</span>
        </div>
      </div>

      <!-- 右侧配置面板 -->
      <div class="form-panel">

        <!-- 通用信息 -->
        <h3 class="section-title">基础信息</h3>

        <el-form :model="form" label-width="120px" class="form-block">

          <el-form-item label="身份源名称">
            <el-input v-model="form.name" />
          </el-form-item>

          <el-form-item label="描述">
            <el-input v-model="form.desc" type="textarea" />
          </el-form-item>

          <!-- 根据类型动态表单 -->
          <template v-if="form.type === 'ldap' || form.type === 'ad'">

            <h3 class="section-title">连接配置</h3>

            <el-form-item label="服务器地址">
              <el-input v-model="form.server" placeholder="ldap://example.com" />
            </el-form-item>

            <el-form-item label="端口">
              <el-input v-model="form.port" placeholder="389" />
            </el-form-item>

            <el-form-item label="Base DN">
              <el-input v-model="form.baseDN" placeholder="dc=example,dc=com" />
            </el-form-item>

            <el-form-item label="管理员 DN">
              <el-input v-model="form.bindDN" placeholder="cn=admin,dc=example,dc=com" />
            </el-form-item>

            <el-form-item label="密码">
              <el-input v-model="form.password" show-password />
            </el-form-item>

          </template>

          <!-- OIDC 配置 -->
          <template v-if="form.type === 'oidc'">

            <h3 class="section-title">OIDC 设置</h3>

            <el-form-item label="Issuer">
              <el-input v-model="form.issuer" placeholder="https://idp.example.com" />
            </el-form-item>

            <el-form-item label="Client ID">
              <el-input v-model="form.clientId" />
            </el-form-item>

            <el-form-item label="Client Secret">
              <el-input v-model="form.clientSecret" show-password />
            </el-form-item>

            <el-form-item label="Scope">
              <el-input v-model="form.scope" placeholder="openid profile email" />
            </el-form-item>

          </template>

          <!-- SAML 配置 -->
          <template v-if="form.type === 'saml'">

            <h3 class="section-title">SAML 设置</h3>

            <el-form-item label="IdP Metadata URL">
              <el-input v-model="form.metadataUrl" placeholder="https://idp.example.com/metadata" />
            </el-form-item>

            <el-form-item label="Entity ID">
              <el-input v-model="form.entityId" />
            </el-form-item>

            <el-form-item label="证书">
              <el-input
                v-model="form.cert"
                type="textarea"
                placeholder="-----BEGIN CERTIFICATE-----"
              />
            </el-form-item>

          </template>

        </el-form>

        <!-- 底部按钮 -->
        <div class="footer-buttons">
          <el-button @click="cancel">取消</el-button>
          <el-button type="warning" @click="testConnect">测试连接</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>

      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "IdentitySourceCreate",

  data() {
    return {
      types: [
        { label: "LDAP", value: "ldap",  },
        { label: "Active Directory", value: "ad", },
        { label: "OIDC 身份源", value: "oidc",  },
        { label: "SAML 身份源", value: "saml",  },
      ],

      form: {
        type: "ldap",
        name: "",
        desc: "",

        // LDAP/AD
        server: "",
        port: "",
        baseDN: "",
        bindDN: "",
        password: "",

        // OIDC
        issuer: "",
        clientId: "",
        clientSecret: "",
        scope: "openid profile email",

        // SAML
        metadataUrl: "",
        entityId: "",
        cert: "",
      }
    };
  },

  methods: {
    selectType(t) {
      this.form.type = t;
    },

    testConnect() {
      this.$message.success("测试连接成功（模拟）");
    },

    save() {
      this.$message.success("已保存身份源：" + this.form.name);
    },

    cancel() {
      this.$router.push("/identity-source");
    }
  }
};
</script>

<style lang="scss" scoped>
.identity-create-page {
  padding: 30px 40px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
  }

  .page-desc {
    margin-top: 4px;
    color: #666;
    margin-bottom: 20px;
  }

  .create-body {
    display: flex;
  }

  /* 左侧类型选择 */
  .type-select {
    width: 220px;
    margin-right: 30px;

    .type-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      border: 1px solid #eee;
      border-radius: 10px;
      margin-bottom: 12px;
      cursor: pointer;
      transition: 0.2s;

      img {
        width: 28px;
        margin-right: 12px;
      }

      &:hover {
        background: #f7f9fc;
      }

      &.active {
        border-color: #409eff;
        background: #ecf5ff;
      }
    }
  }

  /* 右侧表单 */
  .form-panel {
    flex: 1;
    background: #fff;
    padding: 26px;
    border-radius: 12px;
    border: 1px solid #eee;

    .section-title {
      font-size: 17px;
      font-weight: 600;
      margin: 20px 0 10px;
    }
  }

  /* 底部按钮 */
  .footer-buttons {
    text-align: right;
    margin-top: 20px;

    button {
      margin-left: 10px;
    }
  }
}
</style>
