<template>
  <el-dialog
    :visible.sync="visibleLocal"
    custom-class="spa-dialog"
    :width="dialogWidth"
    :before-close="handleBeforeClose"
    destroy-on-close
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    title="单页应用程序"
  >
    <div class="spa-create-container">
      <!-- 左侧主内容 -->
      <div class="main-panel">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-width="110px"
          class="form-box"
          label-position="top"
        >
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="我的应用" clearable />
          </el-form-item>

          <el-form-item label="授权重定向 URL" prop="redirectUrls">
            <div class="redirect-url-box">
              <el-input
                v-model="redirectInput"
                placeholder="https://explame.com/login"
                clearable
                @keyup.enter.native="addUrl"
              />
              <el-button class="add-url-btn" @click="addUrl">+</el-button>
            </div>

            <div v-if="form.redirectUrls.length" class="url-list">
              <div
                v-for="(u, i) in form.redirectUrls"
                :key="u + i"
                class="url-item"
              >
                <span class="url-text">{{ u }}</span>
                <i class="el-icon-close url-remove" @click="removeUrl(i)" />
              </div>
            </div>
          </el-form-item>
        </el-form>

        <div class="footer">
          <el-button class="cancel-btn" @click="onCancel">取消</el-button>
          <el-button
            type="primary"
            :loading="submitting"
            class="create-btn"
            @click="onSubmit"
          >
            创建
          </el-button>
        </div>
      </div>

      <!-- 右侧帮助栏 -->
      <div class="help-panel">
        <div class="help-block">
          <div class="help-title">名称</div>
          <div class="help-text">
            用于识别您的应用程序的唯一名称。<br>
            例如：我的应用程序
          </div>
        </div>

        <div class="help-block">
          <div class="help-title">授权重定向 URL</div>
          <div class="help-text">
            身份验证后，授权码将被发送到该 URL。<br>
            例如：https://explame.com/login
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'CreateSpaDialog',
  model: {
    prop: 'visible',
    event: 'update:visible'
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    initial: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      visibleLocal: false,
      dialogWidth: '70%',
      form: {
        name: '',
        redirectUrls: [],
        share: false
      },
      redirectInput: '',
      submitting: false,
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        redirectUrls: [
          {
            validator: (rule, value, callback) => {
              if (!value || !value.length) {
                callback(new Error('请至少添加一个重定向 URL'))
                return
              }
              callback()
            },
            required: true,
            trigger: 'change'
          }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.visibleLocal = val
      if (val) this.resetFromInitial()
    },
    visibleLocal(val) {
      // 向父组件同步 visible（支持 .sync）
      this.$emit('update:visible', val)
    }
  },
  methods: {
    resetFromInitial() {
      // 如果父组件传初始值，合并
      if (this.initial && Object.keys(this.initial).length) {
        this.form = Object.assign(
          { name: '', redirectUrls: [], share: false },
          this.initial
        )
      } else {
        this.form = { name: '', redirectUrls: [], share: false }
      }
      this.redirectInput = ''
      this.submitting = false
      // reset validation state if any
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },

    addUrl() {
      const v = (this.redirectInput || '').trim()
      if (!v) {
        this.$message.warning('请输入 URL')
        return
      }
      if (!this.validateUrlFormat(v)) {
        this.$message.warning('URL 格式不正确')
        return
      }
      if (this.form.redirectUrls.indexOf(v) === -1) {
        this.form.redirectUrls.push(v)
      }
      this.redirectInput = ''
    },

    removeUrl(i) {
      this.form.redirectUrls.splice(i, 1)
    },

    validateUrlFormat(u) {
      // 简单 URL 校验
      const re = /^(https?:\/\/)[^\s/$.?#].[^\s]*$/i
      return re.test(u)
    },

    onCancel() {
      this.visibleLocal = false
    },

    handleBeforeClose(done) {
      // 可在此做拦截
      done()
    },

    async onSubmit() {
      // 表单校验
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (!this.form.redirectUrls.length) {
          this.$message.warning('请至少添加一个重定向 URL')
          return
        }

        // 模拟提交
        try {
          this.submitting = true

          // === 在这里调用真正的 API ===
          // const res = await api.createApp(this.form);
          // const newId = res.data.id;

          // 模拟网络请求
          await new Promise(resolve => setTimeout(resolve, 700))
          const appId = Date.now() // 模拟返回 id

          this.$message.success('创建成功')
          // 关闭弹窗
          this.visibleLocal = false
          // 通知父组件创建成功并传回 id
          this.$emit('created', appId)
        } catch (err) {
          this.$message.error('创建失败')
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style lang="scss">
.spa-dialog {
  border-radius: 4px !important;
  padding: 0 !important;
  width: 80% !important; /* 随屏幕比例变化 */
  max-width: 960px; /* PC 大屏不至于太宽 */
  min-width: 520px; /* 小屏防止挤压 */

  .el-dialog__header {
    padding: 16px 24px 12px;
  }

  .el-dialog__body {
    padding: 0px;
  }
}

.spa-create-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(
      to bottom,
      rgba(0, 0, 0, 0.08),
      rgba(0, 0, 0, 0)
  );
}

.spa-create-container {
  display: flex;
  padding: 0;
  min-height: 480px;
  box-sizing: border-box;
  position: relative;

  /* 自动布局：左大右小，保持自适应 */
  @media (max-width: 980px) {
    flex-direction: column;
  }

  .main-panel {
    flex: 1;
    padding: 0px 36px 32px 36px;
    background: #fff;
    border-radius: 4px !important;

    .form-box {
      margin-top: 12px;

      .el-form-item {
        margin-bottom: 22px;
      }
    }

    .redirect-url-box {
      display: flex;
      align-items: center;

      .add-url-btn {
        width: 42px;
        height: 36px;
        margin-left: 10px;
        border-radius: 8px;
        padding: 0;
      }
    }

    .url-list {
      margin-top: 12px;

      .url-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        background: #f7f9fb;
        padding: 10px 12px;
        border-radius: 8px;
        margin-bottom: 10px;
        border: 1px solid #eee;

        .url-text {
          font-size: 13px;
          color: #333;
          word-break: break-all;
        }

        .url-remove {
          color: #999;
          cursor: pointer;
          font-size: 14px;

          &:hover {
            color: #ff5a53;
          }
        }
      }
    }

    .footer {
      margin-top: 30px;
      text-align: right;

      .cancel-btn {
        margin-right: 14px;
        padding: 9px 18px;
        border-radius: 8px;

        background: #fff;
        color: #606266;
        border: 1px solid #dcdfe6;

        font-weight: 400;

        transition: background 0.15s ease, border-color 0.15s ease;

        &:hover {
          background: #f5f7fa;
          border-color: #c0c4cc;
        }
      }

      .create-btn {
        padding: 10px 22px;
        border-radius: 8px;

        background: #3a7afe; /* 主品牌蓝 */
        color: #fff;
        border: none;

        font-weight: 500;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);

        transition: background 0.15s ease, box-shadow 0.15s ease;

        &:hover {
          background: #2f6ae5;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
        }

        &:active {
          background: #2a5fd0;
        }
      }

    }
  }

  .help-panel {
    width: 320px;
    min-width: 260px;
    background: #fafafa;
    border-radius: 4px !important;
    padding: 28px 26px;
    box-shadow: -4px 0 8px -4px rgba(0, 0, 0, 0.08);

    @media (max-width: 980px) {
      width: 100%;
      border-left: none;
      border-top: 1px solid #eee;
    }

    .help-block {
      margin-bottom: 26px;

      .help-title {
        font-weight: 800;
        margin-bottom: 6px;
        color: #222;
      }

      .help-text {
        color: #666;
        font-size: 13px;
        line-height: 20px;
      }
    }
  }
}

</style>
