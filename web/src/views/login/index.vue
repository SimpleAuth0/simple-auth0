<template>
  <div class="login-root">

    <!-- 左侧插画区域 -->
    <div class="illustration">
      <div class="title-block">
        <h1 class="brand">SimpleAuth0</h1>
        <p class="subtitle">统一身份认证 · 更简单、更安全</p>
      </div>

      <img class="illu-img" :src="require('@/assets/img/login.svg')" alt="illustration" />
    </div>

    <!-- 右侧登录卡片 -->
    <div class="login-area">
      <div class="login-card">
        <h2 class="login-title">登录你的账户</h2>

        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
            >
              <i slot="prefix" class="el-icon-user" />
            </el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="请输入密码"
              :type="passwordVisible ? 'text' : 'password'"
            >
              <!-- prefix icon -->
              <i slot="prefix" class="el-icon-lock" />

              <!-- suffix eye icon -->
              <i
                slot="suffix"
                :class="passwordVisible ? 'el-icon-view' : 'el-icon-view-off'"
                class="password-icon"
                @click="passwordVisible = !passwordVisible"
              />
            </el-input>
          </el-form-item>

          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      passwordVisible: false,
      loading: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return

        this.loading = true

        this.$store.dispatch('user/login', this.loginForm)
          .then(() => {
            this.$router.push({ path: this.redirect || '/' })
          })
          .finally(() => {
            this.loading = false
          })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-root {
  height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #0D1B2A, #1B263B);
  overflow: hidden;
}

/* ================= 左侧插画区域 ================= */
.illustration {
  flex: 1.15;
  color: #fff;
  padding: 80px 60px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;

  background: linear-gradient(145deg, rgba(75, 123, 255, 0.15), rgba(124, 58, 237, 0.15));
  backdrop-filter: blur(10px);

  .title-block {
    margin-bottom: 50px;

    .brand {
      font-size: 52px;
      font-weight: 700;
      background: linear-gradient(90deg, #4B7BFF, #7C3AED);
      -webkit-background-clip: text;
      color: transparent;
    }

    .subtitle {
      font-size: 20px;
      opacity: 0.9;
      margin-top: 10px;
    }
  }

  .illu-img {
    width: 80%;
    max-width: 460px;
    opacity: 0.95;
    align-self: center;
  }
}

/* ================= 右侧登录区域 ================= */
.login-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;

  .login-card {
    width: 380px;
    padding: 40px 35px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 14px;
    backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.15);

    .login-title {
      text-align: center;
      color: #fff;
      margin-bottom: 30px;
      font-size: 22px;
      font-weight: 600;
    }

    .login-btn {
      width: 100%;
      margin-top: 5px;
      height: 45px;
      border-radius: 8px;
      background: linear-gradient(90deg, #4B7BFF, #7C3AED);
      border: none;
      font-size: 16px;
      font-weight: 500;
    }

    .password-icon {
      cursor: pointer;
      color: #ddd;
      font-size: 18px;
    }
  }
}

/* ================= 小屏适配 ================= */
@media (max-width: 900px) {
  .illustration {
    display: none;
  }
  .login-area {
    flex: 1;
  }
}
</style>
