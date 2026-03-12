<template>
  <div class="app-detail-page">

    <!-- 顶部基础信息栏 -->
    <div class="app-header">
      <el-button
        type="text"
        icon="el-icon-arrow-left"
        class="back-btn"
        @click="$router.back()"
      >返回
      </el-button>

      <div class="app-meta">
        <!-- 应用图标 -->
        <div class="app-avatar">
          <img
            v-if="app.logoUrl"
            :src="app.logoUrl"
            alt="app logo"
          >
          <div v-else class="avatar-fallback">
            {{ appInitial }}
          </div>
        </div>
        <!-- 应用信息 -->
        <div class="app-info">
          <div class="app-name">{{ app.name }}</div>
          <el-tag size="mini" class="app-type">
            {{ app.type }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 新建引导 Banner -->
    <el-alert
      v-if="isFromCreate && showGuide"
      type="success"
      show-icon
      closable
      class="guide-banner"
      title="应用已创建成功"
      description="请完成协议配置、API 授权和角色分配，以便应用可以正常使用。"
      @close="showGuide = false"
    />

    <!-- Tabs -->
    <el-tabs v-model="activeTab" class="detail-tabs">

      <!-- 概览 -->
      <el-tab-pane label="概览" name="overview">
        <div class="app-general">

          <!-- 基本信息 -->
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
            label-width="120px"
            class="general-form"
            label-position="top"
          >
            <!-- 名称 -->
            <el-form-item label="名称" prop="name" required>
              <el-input
                v-model="form.name"
                placeholder="请输入应用程序名称"
                maxlength="64"
              />
            </el-form-item>

            <!-- 描述 -->
            <el-form-item label="描述">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                placeholder="请输入应用程序的描述"
              />
            </el-form-item>

            <!-- 标识 / Logo URL -->
            <el-form-item label="标识">
              <el-input
                v-model="form.logoUrl"
                placeholder="https://myapp-resources.io/my_app_image.png"
              />
              <div class="form-tip">
                应用的图片 URL。如果未提供，我们将显示生成的缩略图。建议尺寸：200×200 像素。
              </div>
            </el-form-item>

            <el-divider />

            <!-- 可发现的应用程序 -->
            <el-form-item label="可发现的应用程序">
              <el-switch v-model="form.discoverable" />
              <div class="form-tip">
                如果启用，客户端可以通过“我的账户门户”访问此应用程序。
              </div>
            </el-form-item>

            <!-- 可发现的群组 -->
            <el-form-item v-if="form.discoverable" label="可发现的群组">
              <el-radio-group v-model="form.discoveryScope">
                <el-radio label="ALL">
                  组织中的每个人都可以发现这个应用程序。
                </el-radio>
                <el-radio label="ASSIGNED">
                  只有部分指定用户才能发现此应用程序。
                </el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 访问网址 -->
            <el-form-item
              label="访问网址"
              prop="homeUrl"
              required
            >
              <el-input
                v-model="form.homeUrl"
                placeholder="https://myapp.io/home"
              />
              <el-alert
                v-if="homeUrlError"
                type="error"
                :closable="false"
                class="url-error"
              >
                必须提供有效的访问 URL 才能使应用程序可被发现。
              </el-alert>
              <div class="form-tip">
                应用程序的登录页面 URL。它将用于应用程序目录和发现流程。
                如果登录页面被禁用，用户将通过该 URL 重定向到客户端应用程序。
              </div>
            </el-form-item>
          </el-form>

          <!-- 底部操作 -->
          <div class="page-footer">
            <el-button @click="reset">重置</el-button>
            <el-button
              type="primary"
              :loading="saving"
              @click="save"
            >
              保存
            </el-button>
          </div>
        </div>

      </el-tab-pane>

      <!-- 协议 -->
      <el-tab-pane label="协议" name="protocol">
        <div class="protocol-section">
          <!-- 基本信息 -->
          <el-card class="config-card" shadow="never">
            <div slot="header">
              <span>基本信息</span>
            </div>

            <el-form label-width="160px" label-position="left">
              <!-- 客户端ID -->
              <el-form-item label="客户端ID">
                <el-input v-model="protocolForm.clientId" readonly style="max-width: 400px">
                  <el-button slot="append" icon="el-icon-document-copy" @click="copyClientId">复制</el-button>
                </el-input>
              </el-form-item>

              <!-- 授权类型 -->
              <el-form-item label="授权类型">
                <el-checkbox-group v-model="protocolForm.grantTypes">
                  <el-checkbox label="authorization_code">授权码模式</el-checkbox>
                  <el-checkbox label="implicit">隐式授权</el-checkbox>
                  <el-checkbox label="password">密码凭证</el-checkbox>
                  <el-checkbox label="client_credentials">客户端凭证</el-checkbox>
                  <el-checkbox label="refresh_token">刷新令牌</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <!-- 授权重定向URL -->
              <el-form-item label="授权重定向URL">
                <div v-for="(url, index) in protocolForm.redirectUris" :key="index" class="dynamic-input">
                  <el-input v-model="protocolForm.redirectUris[index]" placeholder="https://example.com/callback" style="max-width: 500px">
                    <el-button v-if="protocolForm.redirectUris.length > 1" slot="append" icon="el-icon-delete" @click="removeRedirectUri(index)" />
                  </el-input>
                </div>
                <el-button type="text" icon="el-icon-plus" @click="addRedirectUri">添加重定向URL</el-button>
              </el-form-item>

              <!-- 允许的来源 -->
              <el-form-item label="允许的来源">
                <div v-for="(origin, index) in protocolForm.allowedOrigins" :key="index" class="dynamic-input">
                  <el-input v-model="protocolForm.allowedOrigins[index]" placeholder="https://example.com" style="max-width: 500px">
                    <el-button v-if="protocolForm.allowedOrigins.length > 1" slot="append" icon="el-icon-delete" @click="removeAllowedOrigin(index)" />
                  </el-input>
                </div>
                <el-button type="text" icon="el-icon-plus" @click="addAllowedOrigin">添加来源</el-button>
              </el-form-item>

              <!-- PKCE -->
              <el-form-item label="PKCE">
                <el-checkbox v-model="protocolForm.pkceEnabled">启用PKCE（推荐用于公共客户端）</el-checkbox>
                <div class="form-tip">PKCE可防止授权码拦截攻击</div>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 访问令牌 -->
          <el-card class="config-card token-card" shadow="never">
            <div slot="header">
              <span>访问令牌</span>
            </div>

            <el-form label-width="160px" label-position="left">
              <el-form-item label="令牌类型">
                <el-radio-group v-model="protocolForm.accessTokenType">
                  <el-radio label="JWT">JWT（自包含）</el-radio>
                  <el-radio label="opaque">不透明（引用令牌）</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="令牌过期时间">
                <el-input-number v-model="protocolForm.accessTokenValidity" :min="1" :max="86400" />
                <span class="unit-label">秒</span>
                <div class="form-tip">建议：3600秒（1小时）</div>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 刷新令牌 -->
          <el-card class="config-card token-card" shadow="never">
            <div slot="header">
              <span>刷新令牌</span>
            </div>

            <el-form label-width="160px" label-position="left">
              <el-form-item label="更新刷新令牌">
                <el-radio-group v-model="protocolForm.reuseRefreshToken">
                  <el-radio :label="false">每次刷新都生成新令牌（推荐）</el-radio>
                  <el-radio :label="true">重用刷新令牌</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="刷新令牌有效期">
                <el-input-number v-model="protocolForm.refreshTokenValidity" :min="1" :max="2592000" />
                <span class="unit-label">秒</span>
                <div class="form-tip">建议：604800秒（7天）</div>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- ID令牌 -->
          <el-card class="config-card token-card" shadow="never">
            <div slot="header">
              <span>ID令牌（OIDC）</span>
            </div>

            <el-form label-width="160px" label-position="left">
              <el-form-item label="Audiences">
                <el-input v-model="protocolForm.idTokenAud" placeholder="默认使用客户端ID" style="max-width: 400px" />
                <div class="form-tip">令牌的接收者，通常是客户端ID</div>
              </el-form-item>

              <el-form-item label="ID令牌过期时间">
                <el-input-number v-model="protocolForm.idTokenValidity" :min="1" :max="86400" />
                <span class="unit-label">秒</span>
                <div class="form-tip">建议：3600秒（1小时）</div>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 保存按钮 -->
          <div class="protocol-footer">
            <el-button @click="resetProtocol">重置</el-button>
            <el-button type="primary" :loading="savingProtocol" @click="saveProtocol">保存配置</el-button>
          </div>
        </div>
      </el-tab-pane>

      <!-- API 授权 -->
      <el-tab-pane label="API 授权" name="api">
        <div class="api-auth-section">
          <!-- 头部标题 -->
          <div class="section-header">
            <h3 class="section-title">管理对 API 资源的访问</h3>
            <el-button size="small" type="primary" icon="el-icon-plus" @click="openApiAuthDialog">授权API资源</el-button>
          </div>

          <!-- 已授权的API资源列表 -->
          <el-card class="config-card" shadow="never">
            <div slot="header">
              <span>已授权的API资源</span>
            </div>

            <el-table v-loading="apiAuthLoading" :data="authorizedApis" style="width: 100%">
              <el-table-column prop="apiName" label="API资源名称" min-width="150">
                <template slot-scope="{ row }">
                  <div class="api-name-cell">
                    <i class="el-icon-s-operation" style="margin-right: 8px; color: #409eff;" />
                    <span>{{ row.apiName }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="apiPath" label="API路径" min-width="200">
                <template slot-scope="{ row }">
                  <code class="api-path">{{ row.apiPath }}</code>
                </template>
              </el-table-column>
              <el-table-column prop="scope" label="授权范围" width="200">
                <template slot-scope="{ row }">
                  <el-tag v-for="s in row.scope" :key="s" size="small" style="margin-right: 5px;">{{ s }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="policy" label="授权政策" width="150">
                <template slot-scope="{ row }">
                  <el-tag type="info" size="small">{{ row.policy }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="authTime" label="授权时间" width="160" />
              <el-table-column label="操作" width="120" fixed="right">
                <template slot-scope="{ row }">
                  <el-button type="text" @click="editApiAuth(row)">编辑</el-button>
                  <el-popconfirm title="确定取消该API授权吗？" @confirm="removeApiAuth(row)">
                    <el-button slot="reference" type="text" class="danger">移除</el-button>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="authorizedApis.length === 0" description="暂无授权的API资源" />
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 角色 -->
      <el-tab-pane label="角色" name="role">
        <div class="role-section">
          <!-- 头部标题 -->
          <div class="section-header">
            <h3 class="section-title">角色</h3>
          </div>

          <!-- 角色受众选择 -->
          <el-card class="config-card" shadow="never">
            <div slot="header">
              <span>角色受众</span>
            </div>

            <el-form label-width="140px" label-position="left">
              <el-form-item label="受众类型">
                <el-radio-group v-model="roleAudience.type">
                  <el-radio label="application">应用</el-radio>
                  <el-radio label="organization">组织</el-radio>
                  <el-radio label="both">应用和组织</el-radio>
                </el-radio-group>
                <div class="form-tip">选择角色适用的受众范围</div>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 分配的角色 -->
          <el-card class="config-card role-card" shadow="never">
            <div slot="header">
              <span>分配的角色</span>
              <el-button type="primary" size="small" icon="el-icon-plus" style="float: right;" @click="openRoleDialog">添加角色</el-button>
            </div>

            <el-table :data="assignedRoles" style="width: 100%">
              <el-table-column prop="roleName" label="角色名称" min-width="150">
                <template slot-scope="{ row }">
                  <div class="role-name-cell">
                    <el-tag :type="getRoleType(row.roleCode)" size="small" effect="dark" style="margin-right: 8px;">
                      {{ row.roleName.charAt(0) }}
                    </el-tag>
                    <span>{{ row.roleName }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="roleCode" label="角色编码" width="150" />
              <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
              <el-table-column prop="assignTime" label="分配时间" width="160" />
              <el-table-column label="操作" width="120" fixed="right">
                <template slot-scope="{ row }">
                  <el-popconfirm title="确定移除该角色吗？" @confirm="removeRole(row)">
                    <el-button slot="reference" type="text" class="danger">移除</el-button>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="assignedRoles.length === 0" description="暂无分配的角色" />
          </el-card>

          <!-- 保存按钮 -->
          <div class="role-footer">
            <el-button @click="resetRoleConfig">重置</el-button>
            <el-button type="primary" :loading="savingRole" @click="saveRoleConfig">保存配置</el-button>
          </div>
        </div>
      </el-tab-pane>

    </el-tabs>

    <!-- API授权添加/编辑弹窗 -->
    <el-dialog :title="isEditApiAuth ? '编辑API授权' : '授权API资源'" :visible.sync="apiAuthDialogVisible" width="600px">
      <el-form ref="apiAuthForm" :model="apiAuthForm" label-width="120px" :rules="apiAuthRules">
        <el-form-item label="API资源" prop="apiId">
          <el-select v-model="apiAuthForm.apiId" placeholder="选择API资源" style="width: 100%" :disabled="isEditApiAuth">
            <el-option
              v-for="api in availableApis"
              :key="api.id"
              :label="api.name + ' (' + api.path + ')'"
              :value="api.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="授权范围" prop="scope">
          <el-checkbox-group v-model="apiAuthForm.scope">
            <el-checkbox label="read">读取</el-checkbox>
            <el-checkbox label="write">写入</el-checkbox>
            <el-checkbox label="delete">删除</el-checkbox>
            <el-checkbox label="admin">管理</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="授权政策" prop="policy">
          <el-select v-model="apiAuthForm.policy" placeholder="选择授权政策" style="width: 100%">
            <el-option label="RBAC访问控制" value="RBAC" />
            <el-option label="ABAC属性控制" value="ABAC" />
            <el-option label="自定义策略" value="CUSTOM" />
          </el-select>
          <div class="form-tip">默认使用RBAC（基于角色的访问控制）</div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="apiAuthDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveApiAuth">确定</el-button>
      </div>
    </el-dialog>

    <!-- 添加角色弹窗 -->
    <el-dialog title="添加角色" :visible.sync="roleDialogVisible" width="500px">
      <el-form ref="roleForm" :model="roleForm" label-width="100px" :rules="roleRules">
        <el-form-item label="选择角色" prop="roleId">
          <el-select v-model="roleForm.roleId" placeholder="选择要分配的角色" style="width: 100%">
            <el-option
              v-for="role in availableRoles"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            >
              <div style="display: flex; align-items: center;">
                <el-tag :type="getRoleType(role.roleCode)" size="mini" effect="dark" style="margin-right: 8px;">
                  {{ role.roleName.charAt(0) }}
                </el-tag>
                <span>{{ role.roleName }}</span>
                <span style="color: #909399; margin-left: 8px; font-size: 12px;">{{ role.roleCode }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AppDetail',

  props: {
    appId: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      activeTab: 'overview',
      showGuide: true,

      // 模拟应用数据
      app: {
        name: '测试',
        type: '单页应用程序',
        logoUrl: '',
        description: '',
        discoverable: true,
        discoveryScope: 'ALL', // ALL | ASSIGNED
        homeUrl: '',
        protocolConfigured: false,
        apiAuthorized: false,
        roleAssigned: false
      },

      // 表单数据
      form: {
        name: '',
        description: '',
        logoUrl: '',
        discoverable: true,
        discoveryScope: 'ALL',
        homeUrl: ''
      },

      // 协议配置表单
      protocolForm: {
        clientId: 'app-123456-abcdef',
        grantTypes: ['authorization_code', 'refresh_token'],
        redirectUris: ['https://example.com/callback'],
        allowedOrigins: ['https://example.com'],
        pkceEnabled: true,
        accessTokenType: 'JWT',
        accessTokenValidity: 3600,
        reuseRefreshToken: false,
        refreshTokenValidity: 604800,
        idTokenAud: '',
        idTokenValidity: 3600
      },

      savingProtocol: false,
      homeUrlError: false,

      // API授权相关数据
      apiAuthLoading: false,
      authorizedApis: [
        {
          id: 1,
          apiId: 101,
          apiName: '用户列表',
          apiPath: '/api/v1/users',
          scope: ['read', 'write'],
          policy: 'RBAC',
          authTime: '2023-12-01 10:30:00'
        },
        {
          id: 2,
          apiId: 102,
          apiName: '角色管理',
          apiPath: '/api/v1/roles',
          scope: ['read'],
          policy: 'RBAC',
          authTime: '2023-12-01 11:15:00'
        }
      ],
      availableApis: [
        { id: 101, name: '用户列表', path: '/api/v1/users' },
        { id: 102, name: '角色管理', path: '/api/v1/roles' },
        { id: 103, name: 'API资源管理', path: '/api/v1/resources' },
        { id: 104, name: '审计日志', path: '/api/v1/logs' }
      ],
      apiAuthDialogVisible: false,
      isEditApiAuth: false,
      apiAuthForm: {
        apiId: null,
        scope: ['read'],
        policy: 'RBAC'
      },
      apiAuthRules: {
        apiId: [{ required: true, message: '请选择API资源', trigger: 'change' }],
        scope: [{ required: true, message: '请选择授权范围', trigger: 'change' }],
        policy: [{ required: true, message: '请选择授权政策', trigger: 'change' }]
      },

      // 角色相关数据
      roleAudience: {
        type: 'application'
      },
      assignedRoles: [
        {
          id: 1,
          roleId: 1,
          roleName: '系统管理员',
          roleCode: 'ADMIN',
          description: '拥有全部权限',
          assignTime: '2023-12-01 10:00:00'
        },
        {
          id: 2,
          roleId: 2,
          roleName: '普通用户',
          roleCode: 'USER',
          description: '仅可访问基础功能',
          assignTime: '2023-12-01 10:30:00'
        }
      ],
      availableRoles: [
        { id: 1, roleName: '系统管理员', roleCode: 'ADMIN', description: '拥有全部权限' },
        { id: 2, roleName: '普通用户', roleCode: 'USER', description: '仅可访问基础功能' },
        { id: 3, roleName: '审计员', roleCode: 'AUDITOR', description: '仅可查看日志和报表' },
        { id: 4, roleName: '访客', roleCode: 'GUEST', description: '仅可查看公开内容' }
      ],
      roleDialogVisible: false,
      roleForm: {
        roleId: null
      },
      roleRules: {
        roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
      },
      savingRole: false,

      rules: {
        name: [
          { required: true, message: '请输入应用程序名称', trigger: 'blur' }
        ],
        homeUrl: [
          {
            validator: (rule, val, cb) => {
              if (!val) {
                cb(new Error('请输入访问网址'))
              } else if (!/^https?:\/\/.+/i.test(val)) {
                cb(new Error('访问网址格式不正确'))
              } else {
                cb()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },

  computed: {
    isFromCreate() {
      return this.$route.query.from === 'create'
    },

    appInitial() {
      if (!this.app || !this.app.name) return '?'
      return this.app.name.trim().charAt(0).toUpperCase()
    },

    checklist() {
      return [
        {
          key: 'protocol',
          label: '配置协议',
          done: this.app.protocolConfigured,
          tab: 'protocol'
        },
        {
          key: 'api',
          label: '授权 API',
          done: this.app.apiAuthorized,
          tab: 'api'
        },
        {
          key: 'role',
          label: '分配角色',
          done: this.app.roleAssigned,
          tab: 'role'
        }
      ]
    }
  },

  created() {
    if (this.isFromCreate) {
      this.activeTab = 'protocol'
    }
    this.fetchDetail()
  },

  methods: {
    goTo(tab) {
      this.activeTab = tab
    },

    async fetchDetail() {
      // TODO: 调用 API 获取应用详情
      // const res = await api.getApp(this.appId)

      // mock
      this.form = {
        name: '333',
        description: '',
        logoUrl: '',
        discoverable: true,
        discoveryScope: 'ALL',
        homeUrl: ''
      }

      // 加载协议配置
      this.loadProtocolConfig()
    },

    loadProtocolConfig() {
      // TODO: 从API加载协议配置
    },

    // 协议配置方法
    copyClientId() {
      this.$copyText(this.protocolForm.clientId).then(() => {
        this.$message.success('客户端ID已复制')
      }).catch(() => {
        // 降级方案
        const input = document.createElement('input')
        input.value = this.protocolForm.clientId
        document.body.appendChild(input)
        input.select()
        document.execCommand('copy')
        document.body.removeChild(input)
        this.$message.success('客户端ID已复制')
      })
    },

    addRedirectUri() {
      this.protocolForm.redirectUris.push('')
    },

    removeRedirectUri(index) {
      this.protocolForm.redirectUris.splice(index, 1)
    },

    addAllowedOrigin() {
      this.protocolForm.allowedOrigins.push('')
    },

    removeAllowedOrigin(index) {
      this.protocolForm.allowedOrigins.splice(index, 1)
    },

    resetProtocol() {
      this.loadProtocolConfig()
      this.$message.info('协议配置已重置')
    },

    saveProtocol() {
      this.savingProtocol = true
      setTimeout(() => {
        this.savingProtocol = false
        this.app.protocolConfigured = true
        this.$message.success('协议配置保存成功')
      }, 800)
    },
    reset() {
      this.fetchDetail()
      this.$nextTick(() => {
        this.$refs.form.clearValidate()
      })
    },

    save() {
      this.$refs.form.validate(async valid => {
        if (!valid) {
          this.homeUrlError = true
          return
        }

        try {
          this.saving = true
          this.homeUrlError = false

          // TODO: API 保存
          // await api.updateApp(this.appId, this.form)

          this.$message.success('保存成功')
        } finally {
          this.saving = false
        }
      })
    },

    // API授权相关方法
    openApiAuthDialog() {
      this.isEditApiAuth = false
      this.apiAuthForm = {
        apiId: null,
        scope: ['read'],
        policy: 'RBAC'
      }
      this.apiAuthDialogVisible = true
    },

    editApiAuth(row) {
      this.isEditApiAuth = true
      this.apiAuthForm = {
        apiId: row.apiId,
        scope: [...row.scope],
        policy: row.policy
      }
      this.apiAuthDialogVisible = true
    },

    saveApiAuth() {
      this.$refs.apiAuthForm.validate(valid => {
        if (valid) {
          if (this.isEditApiAuth) {
            // 编辑模式
            const index = this.authorizedApis.findIndex(item => item.apiId === this.apiAuthForm.apiId)
            if (index > -1) {
              this.authorizedApis[index] = {
                ...this.authorizedApis[index],
                scope: [...this.apiAuthForm.scope],
                policy: this.apiAuthForm.policy
              }
            }
            this.$message.success('API授权已更新')
          } else {
            // 新增模式
            const api = this.availableApis.find(item => item.id === this.apiAuthForm.apiId)
            this.authorizedApis.push({
              id: Date.now(),
              apiId: this.apiAuthForm.apiId,
              apiName: api.name,
              apiPath: api.path,
              scope: [...this.apiAuthForm.scope],
              policy: this.apiAuthForm.policy,
              authTime: new Date().toLocaleString()
            })
            this.$message.success('API资源授权成功')
          }
          this.apiAuthDialogVisible = false
        }
      })
    },

    removeApiAuth(row) {
      const index = this.authorizedApis.findIndex(item => item.id === row.id)
      if (index > -1) {
        this.authorizedApis.splice(index, 1)
        this.$message.success('API授权已移除')
      }
    },

    // 角色相关方法
    getRoleType(code) {
      const typeMap = {
        'ADMIN': 'danger',
        'USER': 'success',
        'AUDITOR': 'warning',
        'GUEST': 'info'
      }
      return typeMap[code] || 'info'
    },

    openRoleDialog() {
      this.roleForm = {
        roleId: null
      }
      this.roleDialogVisible = true
    },

    saveRole() {
      this.$refs.roleForm.validate(valid => {
        if (valid) {
          const role = this.availableRoles.find(item => item.id === this.roleForm.roleId)
          // 检查是否已分配
          const exists = this.assignedRoles.find(item => item.roleId === role.id)
          if (exists) {
            this.$message.warning('该角色已分配')
            return
          }
          this.assignedRoles.push({
            id: Date.now(),
            roleId: role.id,
            roleName: role.roleName,
            roleCode: role.roleCode,
            description: role.description,
            assignTime: new Date().toLocaleString()
          })
          this.$message.success('角色分配成功')
          this.roleDialogVisible = false
        }
      })
    },

    removeRole(row) {
      const index = this.assignedRoles.findIndex(item => item.id === row.id)
      if (index > -1) {
        this.assignedRoles.splice(index, 1)
        this.$message.success('角色已移除')
      }
    },

    resetRoleConfig() {
      this.$message.info('角色配置已重置')
    },

    saveRoleConfig() {
      this.savingRole = true
      setTimeout(() => {
        this.savingRole = false
        this.app.roleAssigned = true
        this.$message.success('角色配置保存成功')
      }, 800)
    }

  }
}
</script>

<style scoped lang="scss">
.app-detail-page {
  padding: 24px 32px;
}

.app-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 20px;

  .back-btn {
    margin-bottom: 8px;
    font-size: 14px;
    color: #606266;
    cursor: pointer;

    padding: 6px 10px;
    border-radius: 8px;

    transition: background 0.15s ease, color 0.15s ease;

    &:hover {
      background: #f5f7fa;
      color: #409eff;
    }
  }

  .app-meta {
    display: flex;
    align-items: center;
  }

  .app-avatar {
    width: 58px;
    height: 58px;
    margin-right: 12px;

    img {
      width: 100%;
      height: 100%;
      border-radius: 8px;
      object-fit: cover;
    }

    .avatar-fallback {
      width: 100%;
      height: 100%;
      border-radius: 8px;
      background: #f2f3f5;
      color: #606266;
      font-size: 20px;
      font-weight: 600;

      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .app-info {
    display: flex;
    flex-direction: column;

    .app-name {
      font-size: 18px;
      font-weight: 600;
      line-height: 28px;
    }

    .app-type {
      margin-top: 4px;
      align-self: flex-start;
    }
  }

}

.app-general {
  padding: 10px 10px;
  max-width: 900px;
  //border: solid #999999 1px;

  .general-form {
    background: #fff;
  }

  .form-tip {
    font-size: 12px;
    color: #888;
    margin-top: 6px;
    line-height: 18px;
  }

  .url-error {
    margin: 8px 0;
  }

  .page-footer {
    margin-top: 32px;
    padding-top: 16px;
    border-top: 1px solid #eee;
    text-align: right;
  }
}

.guide-banner {
  margin-bottom: 16px;
}

.detail-tabs {
  background: #fff;
}

/* 协议配置样式 */
.protocol-section {
  max-width: 1200px;

  .config-card {
    margin-bottom: 20px;
    border-radius: 8px;

    ::v-deep .el-card__header {
      padding: 15px 20px;
      font-weight: 600;
      border-bottom: 1px solid #ebeef5;
    }

    .el-form-item {
      margin-bottom: 20px;

      &__label {
        font-weight: 500;
        color: #606266;
      }
    }

    .dynamic-input {
      margin-bottom: 10px;

      .el-input {
        width: calc(100% - 0px);
      }
    }

    .unit-label {
      margin-left: 10px;
      color: #606266;
    }

    .form-tip {
      font-size: 12px;
      color: #909399;
      margin-top: 5px;
      line-height: 1.4;
    }

    .el-checkbox-group {
      .el-checkbox {
        margin-right: 20px;
        margin-bottom: 10px;
      }
    }

    .el-radio-group {
      .el-radio {
        display: block;
        margin-bottom: 10px;
        line-height: 1.5;
      }
    }
  }

  .token-card {
    margin-top: 20px;
  }

  .protocol-footer {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
    text-align: center;

    .el-button {
      padding: 12px 30px;
    }
  }
}

/* API授权样式 */
.api-auth-section,
.role-section {
  max-width: 1200px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }

  .config-card {
    margin-bottom: 20px;
    border-radius: 8px;

    ::v-deep .el-card__header {
      padding: 15px 20px;
      font-weight: 600;
      border-bottom: 1px solid #ebeef5;
    }

    .api-name-cell {
      display: flex;
      align-items: center;
    }

    .api-path {
      background: #f5f7fa;
      padding: 2px 8px;
      border-radius: 4px;
      font-family: monospace;
      font-size: 12px;
    }

    .role-name-cell {
      display: flex;
      align-items: center;
    }
  }

  .role-card {
    margin-top: 20px;
  }

  .role-footer {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
    text-align: center;

    .el-button {
      padding: 12px 30px;
    }
  }
}

.danger {
  color: #f56c6c;
}

.danger:hover {
  color: #f78989;
}
</style>
