<template>
  <el-dialog
    :visible.sync="dialogVisible"
    custom-class="app-create-dialog"
    width="960px"
    destroy-on-close
    :close-on-click-modal="false"
    :show-close="false"
    :title="dialogTitle"
  >
    <!-- 标题下横线 -->
    <div class="dialog-divider" />

    <!-- 内容区 -->
    <div class="dialog-content">
      <!-- 左侧表单区 -->
      <div class="left-panel">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-position="top"
        >
          <el-form-item label="名称" prop="name" required>
            <el-input v-model="form.name" placeholder="我的应用" />
          </el-form-item>

          <el-form-item v-if="appType === 'STANDARD'" label="协议">
            <div class="protocol-list">
              <div
                v-for="p in protocols"
                :key="p.value"
                class="protocol-item"
                :class="{ active: form.protocol === p.value }"
                @click="form.protocol = p.value"
              >
                <img :src="p.icon">
                <span>{{ p.label }}</span>
              </div>
            </div>
          </el-form-item>

          <el-form-item
            v-if="appType !== 'M2M'"
            label="授权重定向 URL"
            prop="redirectUris"
            required
          >
            <div class="redirect-input">
              <el-input
                v-model="redirectInput"
                placeholder="https://myapp.io/login"
                @keyup.enter.native="addRedirect"
              />
              <el-button @click="addRedirect">+</el-button>
            </div>

            <div v-if="form.redirectUris.length" class="uri-list">
              <div
                v-for="(u, i) in form.redirectUris"
                :key="u + i"
                class="uri-item"
              >
                <span>{{ u }}</span>
                <i class="el-icon-close" @click="removeRedirect(i)" />
              </div>
            </div>

            <div class="tip-box">
              <i class="el-icon-info" />
              还没有应用？授权重定向 URL 示例：
              <code>http://localhost:8080/oidc-sample-app/</code>
            </div>
          </el-form-item>
        </el-form>

        <!-- footer -->
        <div class="footer">
          <el-button @click="onCancel">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="onSubmit">创造</el-button>
        </div>
      </div>

      <!-- 右侧帮助区 -->
      <transition name="slide">
        <div v-show="showHelp" class="right-help">
          <div class="help-header">
            <span>帮助</span>
            <i
              class="el-icon-arrow-right toggle-btn"
              @click="showHelp = false"
            />
          </div>

          <div class="help-body">
            <div class="help-block">
              <div class="help-title">名称</div>
              <div class="help-text">
                用于识别您的应用程序的唯一名称。<br>
                例如：我的应用程序
              </div>
            </div>

            <div v-if="appType === 'STANDARD'" class="help-block">
              <div class="help-title">协议</div>
              <div class="help-text">
                用于通过 SSO 登录应用程序的访问配置协议。
              </div>
            </div>

            <div v-if="appType !== 'M2M'" class="help-block">
              <div class="help-title">授权重定向 URL</div>
              <div class="help-text">
                身份验证后，授权码将被发送到该 URL。<br>
                例如：https://myapp.io/login
              </div>
            </div>
          </div>
        </div>
      </transition>

      <!-- 折叠后展开按钮 -->
      <div v-if="!showHelp" class="help-expand">
        <el-button
          icon="el-icon-arrow-left"
          type="text"
          @click="showHelp = true"
        />
      </div>
    </div>
  </el-dialog>
</template>
<script>
export default {
  name: 'CreateAppDialog',
  props: {
    visible: Boolean,
    appType: {
      type: String,
      default: ''
    },
    initial: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      showHelp: true,
      form: {
        name: '',
        protocol: 'oidc',
        redirectUris: []
      },
      redirectInput: '',
      submitting: false,
      protocols: [
        {
          label: 'OpenID Connect',
          value: 'oidc',
          icon: require('@/assets/img/openid.png')
        },
        {
          label: 'OAuth2',
          value: 'oauth2',
          icon: require('@/assets/img/oauth.svg')
        }
      ],
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        redirectUris: [
          {
            validator: (rule, val, cb) => {
              if (this.form.protocol === 'M2M') return cb()
              if (!val || !val.length) {
                cb(new Error('请至少添加一个授权重定向 URL'))
              } else {
                cb()
              }
            }
          }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      const map = {
        SPA: '单页应用程序',
        STANDARD: '基于标准的应用程序',
        MOBILE: '移动应用',
        M2M: 'M2M 应用'
      }
      return map[this.appType] || '创建应用程序'
    },
    dialogVisible: {
      get() {
        console.log('GET dialogVisible')
        return this.visible
      },
      set(val) {
        console.error('🔥 SET dialogVisible:', val)
        this.$emit('update:visible', val)
        if (!val) {
          this.$nextTick(() => {
            this.resetFromInitial()
          })
        }
      }
    }
  },
  methods: {
    resetFromInitial() {
      // 清空表单字段
      this.form.name = ''
      this.form.protocol = ''
      // 清空数组
      this.form.redirectUris.splice(0)
      // 同时清空输入框
      this.redirectInput = ''
      this.submitting = false
      // reset validation state if any
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    addRedirect() {
      const v = this.redirectInput.trim()
      if (v && !this.form.redirectUris.includes(v)) {
        this.form.redirectUris.push(v)
      }
      this.redirectInput = ''
    },
    removeRedirect(i) {
      this.form.redirectUris.splice(i, 1)
    },
    onCancel() {
      this.dialogVisible = false
    },
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.$emit('submit', this.form)
        this.dialogVisible = false
      })
    }
  }
}
</script>
<style lang="scss">
.app-create-dialog {
  .el-dialog__body {
    padding: 0;
  }
}

.dialog-divider {
  height: 1px;
  background: #e5e7eb;
}

.dialog-content {
  display: flex;
  height: 520px;
  position: relative;
}

.left-panel {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
  background: #fff;
}

.footer {
  margin-top: 32px;
  text-align: right;
}

.protocol-list {
  display: flex;
  gap: 16px;

  .protocol-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    width: 140px;
    height: 120px;

    cursor: pointer;
    border-radius: 8px;
    border: 1px solid #ddd;

    img {
      width: 36px;
      height: 36px;
      object-fit: contain;
      margin-bottom: 8px;
    }

    span {
      font-size: 14px;
      text-align: center;
    }

    &.active {
      border: 1px solid #ff7a18;
      background: #fff7f0;
    }
  }

}

.redirect-input {
  display: flex;
  gap: 10px;
}

.uri-list {
  margin-top: 12px;

  .uri-item {
    background: #f7f8fa;
    padding: 8px 10px;
    border-radius: 6px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
  }
}

.tip-box {
  margin-top: 12px;
  background: #eef5ff;
  padding: 10px;
  font-size: 13px;
  border-radius: 6px;
}

.right-help {
  width: 300px;
  flex-shrink: 0;
  background: #fafafa;
  border-left: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.help-header {
  height: 48px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  border-bottom: 1px solid #eee;
}

.help-body {
  padding: 16px;
  overflow-y: auto;
}

.help-block {
  margin-bottom: 24px;

  .help-title {
    font-weight: 600;
    margin-bottom: 6px;
  }

  .help-text {
    font-size: 13px;
    color: #666;
    line-height: 20px;
  }
}

.help-expand {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

/* 向左折叠（右侧面板） */
.slide-enter-active,
.slide-leave-active {
  transition: width 0.2s ease, opacity 0.2s ease;
}

.slide-enter,
.slide-leave-to {
  width: 0;
  opacity: 0;
}

</style>
