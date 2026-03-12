<template>
  <div class="audit-page">
    <!-- 页面标题和统计 -->
    <div class="page-header-section">
      <div class="page-title-area">
        <div>
          <h2 class="page-title">审计日志</h2>
          <p class="page-desc">追踪和审查系统中的所有操作行为，确保合规与安全</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="16" class="stats-row">
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-icon total">
              <i class="el-icon-document-checked" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">总操作数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-icon success">
              <i class="el-icon-circle-check" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.success }}</div>
              <div class="stat-label">成功操作</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-icon failed">
              <i class="el-icon-circle-close" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.failed }}</div>
              <div class="stat-label">失败操作</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-icon warning">
              <i class="el-icon-warning" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.risks }}</div>
              <div class="stat-label">风险事件</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-icon login">
              <i class="el-icon-user" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.logins }}</div>
              <div class="stat-label">今日登录</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card">
            <div class="stat-icon users">
              <i class="el-icon-s-custom" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <el-card shadow="never" class="filter-card">
      <div slot="header" class="filter-header">
        <span><i class="el-icon-search" /> 高级筛选</span>
        <el-button type="text" @click="showMoreFilters = !showMoreFilters">
          {{ showMoreFilters ? '收起' : '展开' }}<i :class="showMoreFilters ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" />
        </el-button>
      </div>

      <el-form :inline="true" :model="searchForm" size="small" label-width="90px">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户/资源/IP"
            style="width: 200px"
            clearable
            prefix-icon="el-icon-search"
          />
        </el-form-item>

        <el-form-item label="时间范围">
          <el-radio-group v-model="timeRangeType" size="small" @change="handleTimeRangeChange">
            <el-radio-button label="today">今日</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="custom">自定义</el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-if="timeRangeType === 'custom'"
            v-model="searchForm.timeRange"
            type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 340px; margin-left: 10px;"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 更多筛选条件 -->
      <el-collapse-transition>
        <div v-show="showMoreFilters" class="more-filters">
          <el-form :inline="true" :model="searchForm" size="small" label-width="90px">
            <el-form-item label="操作类型">
              <el-select v-model="searchForm.action" clearable placeholder="请选择" style="width: 140px">
                <el-option v-for="item in actionTypes" :key="item.value" :label="item.label" :value="item.value">
                  <span style="display: inline-block; width: 100%;">
                    <i :class="item.icon" :style="{ color: item.color }" /> {{ item.label }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="资源类型">
              <el-select v-model="searchForm.resource" clearable placeholder="请选择" style="width: 140px">
                <el-option v-for="item in resourceTypes" :key="item.value" :label="item.label" :value="item.value">
                  <span style="display: inline-block; width: 100%;">
                    <i :class="item.icon" :style="{ color: item.color }" /> {{ item.label }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="操作结果">
              <el-select v-model="searchForm.result" clearable placeholder="请选择" style="width: 140px">
                <el-option label="成功" value="success">
                  <el-tag size="mini" type="success">成功</el-tag>
                </el-option>
                <el-option label="失败" value="failed">
                  <el-tag size="mini" type="danger">失败</el-tag>
                </el-option>
                <el-option label="警告" value="warning">
                  <el-tag size="mini" type="warning">警告</el-tag>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="风险等级">
              <el-select v-model="searchForm.riskLevel" clearable placeholder="请选择" style="width: 140px">
                <el-option label="高风险" value="high">
                  <el-tag size="mini" effect="dark" type="danger">高风险</el-tag>
                </el-option>
                <el-option label="中风险" value="medium">
                  <el-tag size="mini" effect="dark" type="warning">中风险</el-tag>
                </el-option>
                <el-option label="低风险" value="low">
                  <el-tag size="mini" effect="dark" type="success">低风险</el-tag>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="客户端">
              <el-select v-model="searchForm.clientType" clearable placeholder="请选择" style="width: 140px">
                <el-option label="Web" value="web" />
                <el-option label="移动应用" value="mobile" />
                <el-option label="API" value="api" />
                <el-option label="CLI" value="cli" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>

      <!-- 快速筛选标签 -->
      <div class="quick-filters">
        <span class="quick-label">快速筛选:</span>
        <el-tag
          v-for="filter in quickFilters"
          :key="filter.value"
          :class="{ active: currentQuickFilter === filter.value }"
          size="small"
          effect="plain"
          @click="applyQuickFilter(filter.value)"
        >
          {{ filter.label }}
        </el-tag>
      </div>
    </el-card>

    <!-- 日志列表 -->
    <el-card shadow="never" class="log-card">
      <div slot="header" class="log-header">
        <div class="header-left">
          <span>操作日志</span>
          <el-badge :value="total" class="count-badge" />
        </div>
        <div class="header-right">
          <el-button type="primary" size="mini" icon="el-icon-download" @click="exportDialogVisible = true">导出日志</el-button>
          <el-divider direction="vertical" />
          <el-radio-group v-model="viewMode" size="mini">
            <el-radio-button label="list"><i class="el-icon-tickets" /> 列表</el-radio-button>
            <el-radio-button label="timeline"><i class="el-icon-time" /> 时间轴</el-radio-button>
          </el-radio-group>
          <el-dropdown trigger="click" style="margin-left: 10px;">
            <el-button size="mini" icon="el-icon-setting">列设置</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="col in columnOptions" :key="col.prop">
                <el-checkbox v-model="col.visible">{{ col.label }}</el-checkbox>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" v-loading="loading">
        <el-table
          :data="tableData"
          border
          stripe
          size="small"
          highlight-current-row
          @row-click="handleRowClick"
        >
          <el-table-column type="index" width="50" align="center" />

          <el-table-column label="事件" min-width="280" show-overflow-tooltip>
            <template slot-scope="{ row }">
              <div class="event-cell">
                <div class="event-icon" :style="{ background: getActionColor(row.action) + '20' }">
                  <i :class="getActionIcon(row.action)" :style="{ color: getActionColor(row.action) }" />
                </div>
                <div class="event-content">
                  <div class="event-title">
                    <el-link type="primary" @click.stop="showDetail(row)">{{ row.title }}</el-link>
                    <el-tag v-if="row.riskLevel !== 'low'" size="mini" :type="getRiskType(row.riskLevel)" effect="dark" class="risk-tag">
                      {{ row.riskLevel === 'high' ? '高风险' : '中风险' }}
                    </el-tag>
                  </div>
                  <div class="event-desc">{{ row.description }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column v-if="columnOptions[0].visible" label="操作用户" width="150">
            <template slot-scope="{ row }">
              <div class="user-cell">
                <el-avatar size="small" :icon="row.userAvatar || 'el-icon-user-solid'" class="user-avatar" />
                <div class="user-info">
                  <div class="user-name">{{ row.operator }}</div>
                  <div v-if="row.department" class="user-dept">{{ row.department }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column v-if="columnOptions[1].visible" label="资源类型" width="120">
            <template slot-scope="{ row }">
              <span><i :class="getResourceIcon(row.resource)" class="resource-icon" /> {{ row.resourceLabel }}</span>
            </template>
          </el-table-column>

          <el-table-column v-if="columnOptions[2].visible" label="操作结果" width="100" align="center">
            <template slot-scope="{ row }">
              <el-tag :type="getResultType(row.result)" size="mini" effect="dark">
                {{ row.result === 'success' ? '成功' : row.result === 'warning' ? '警告' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column v-if="columnOptions[3].visible" label="IP地址/位置" width="180">
            <template slot-scope="{ row }">
              <div class="ip-cell">
                <span class="ip-address">{{ row.ip }}</span>
                <span v-if="row.location" class="ip-location"><i class="el-icon-location" /> {{ row.location }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column v-if="columnOptions[4].visible" label="客户端" width="140">
            <template slot-scope="{ row }">
              <div class="client-cell">
                <i :class="getClientIcon(row.clientType)" />
                <span>{{ row.clientInfo }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column v-if="columnOptions[5].visible" label="操作时间" width="160">
            <template slot-scope="{ row }">
              <span :title="row.time">{{ formatTime(row.time) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template slot-scope="{ row }">
              <el-button size="mini" type="text" @click.stop="showDetail(row)">详情</el-button>
              <el-button v-if="row.result === 'failed'" size="mini" type="text" @click.stop="showError(row)">查看错误</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            :current-page="currentPage"
            :total="total"
            @size-change="onSizeChange"
            @current-change="onPageChange"
          />
        </div>
      </div>

      <!-- 时间轴视图 -->
      <div v-else v-loading="loading" class="timeline-view">
        <el-timeline>
          <el-timeline-item
            v-for="item in tableData"
            :key="item.id"
            :type="getTimelineType(item)"
            :color="getTimelineColor(item)"
            :timestamp="formatFullTime(item.time)"
          >
            <div class="timeline-card" @click="showDetail(item)">
              <div class="timeline-header">
                <div class="timeline-title">{{ item.title }}</div>
                <el-tag :type="getResultType(item.result)" size="mini" effect="dark">
                  {{ item.result === 'success' ? '成功' : '失败' }}
                </el-tag>
              </div>
              <div class="timeline-content">
                <div class="timeline-info">
                  <span><i class="el-icon-user" /> {{ item.operator }}</span>
                  <span><i class="el-icon-monitor" /> {{ item.ip }}</span>
                </div>
                <div class="timeline-desc">{{ item.description }}</div>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>

        <!-- 加载更多 -->
        <div v-if="tableData.length < total" class="load-more">
          <el-button type="text" :loading="loadingMore" @click="loadMore">加载更多</el-button>
        </div>
      </div>
    </el-card>

    <!-- 日志详情抽屉 -->
    <el-drawer
      :visible.sync="detailDrawerVisible"
      :size="600"
      :with-header="false"
    >
      <div v-if="currentDetail" class="detail-drawer">
        <div class="detail-header">
          <div class="detail-title">
            <i :class="getActionIcon(currentDetail.action)" :style="{ color: getActionColor(currentDetail.action) }" />
            <span>{{ currentDetail.title }}</span>
          </div>
          <el-button icon="el-icon-close" circle size="small" @click="detailDrawerVisible = false" />
        </div>

        <div class="detail-body">
          <!-- 基本信息 -->
          <div class="detail-section">
            <div class="section-title">基本信息</div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="操作ID">{{ currentDetail.id }}</el-descriptions-item>
              <el-descriptions-item label="操作结果">
                <el-tag :type="getResultType(currentDetail.result)" size="mini" effect="dark">
                  {{ currentDetail.result === 'success' ? '成功' : '失败' }}
                </el-tag>
                <span v-if="currentDetail.result === 'failed'" class="error-reason">
                  {{ currentDetail.errorMessage }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item label="风险等级">
                <el-tag :type="getRiskType(currentDetail.riskLevel)" size="mini" effect="dark">
                  {{ currentDetail.riskLevel === 'high' ? '高风险' : currentDetail.riskLevel === 'medium' ? '中风险' : '低风险' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="操作时间">{{ formatFullTime(currentDetail.time) }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 操作者信息 -->
          <div class="detail-section">
            <div class="section-title">操作者信息</div>
            <div class="user-detail">
              <el-avatar size="large" :icon="currentDetail.userAvatar || 'el-icon-user-solid'" />
              <div class="user-detail-info">
                <div class="detail-user-name">{{ currentDetail.operator }}</div>
                <div class="detail-user-meta">
                  <span v-if="currentDetail.department"><i class="el-icon-office-building" /> {{ currentDetail.department }}</span>
                  <span v-if="currentDetail.userRole"><i class="el-icon-s-custom" /> {{ currentDetail.userRole }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 来源信息 -->
          <div class="detail-section">
            <div class="section-title">来源信息</div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="IP地址">
                {{ currentDetail.ip }}
                <el-button type="text" size="mini" @click="blockIP(currentDetail.ip)">加入黑名单</el-button>
              </el-descriptions-item>
              <el-descriptions-item v-if="currentDetail.location" label="地理位置">
                <i class="el-icon-location" /> {{ currentDetail.location }}
              </el-descriptions-item>
              <el-descriptions-item label="客户端类型">{{ currentDetail.clientType }}</el-descriptions-item>
              <el-descriptions-item label="浏览器/设备">{{ currentDetail.clientInfo }}</el-descriptions-item>
              <el-descriptions-item v-if="currentDetail.userAgent" label="User Agent">
                <el-tooltip :content="currentDetail.userAgent" placement="top">
                  <span class="user-agent-text">{{ currentDetail.userAgent }}</span>
                </el-tooltip>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 变更详情 -->
          <div v-if="currentDetail.changes && currentDetail.changes.length > 0" class="detail-section">
            <div class="section-title">变更详情</div>
            <div class="changes-list">
              <div v-for="(change, index) in currentDetail.changes" :key="index" class="change-item">
                <div class="change-field">{{ change.field }}</div>
                <div class="change-values">
                  <div v-if="change.oldValue !== undefined" class="change-old">
                    <span class="change-label">原值:</span>
                    <span class="value old">{{ change.oldValue || '(空)' }}</span>
                  </div>
                  <div class="change-arrow"><i class="el-icon-arrow-right" /></div>
                  <div class="change-new">
                    <span class="change-label">新值:</span>
                    <span class="value new">{{ change.newValue || '(空)' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 原始数据 -->
          <div v-if="currentDetail.rawData" class="detail-section">
            <div class="section-title">
              原始数据
              <el-button type="text" size="mini" @click="copyRawData">复制</el-button>
            </div>
            <pre class="raw-json">{{ formatJson(currentDetail.rawData) }}</pre>
          </div>
        </div>

        <div class="detail-footer">
          <el-button @click="detailDrawerVisible = false">关闭</el-button>
          <el-button type="primary" icon="el-icon-document-copy" @click="exportSingle(currentDetail)">导出此记录</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 导出对话框 -->
    <el-dialog title="导出审计日志" :visible.sync="exportDialogVisible" width="500px">
      <el-form :model="exportForm" label-width="100px">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="exportForm.timeRange"
            type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="exportForm.action" multiple placeholder="全部类型" style="width: 100%">
            <el-option v-for="item in actionTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="exportForm.resource" multiple placeholder="全部资源" style="width: 100%">
            <el-option v-for="item in resourceTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportForm.format">
            <el-radio label="excel">Excel (.xlsx)</el-radio>
            <el-radio label="csv">CSV (.csv)</el-radio>
            <el-radio label="json">JSON (.json)</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="exporting" @click="confirmExport">确认导出</el-button>
      </div>
    </el-dialog>

    <!-- 错误详情对话框 -->
    <el-dialog title="错误详情" :visible.sync="errorDialogVisible" width="500px">
      <div v-if="currentError" class="error-detail">
        <div class="error-icon">
          <i class="el-icon-circle-close" />
        </div>
        <div class="error-message">{{ currentError.errorMessage }}</div>
        <div v-if="currentError.errorStack" class="error-stack">
          <pre>{{ currentError.errorStack }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AuditLog',
  data() {
    return {
      loading: false,
      loadingMore: false,

      // 统计数据
      stats: {
        total: 12547,
        success: 12389,
        failed: 158,
        risks: 23,
        logins: 892,
        activeUsers: 156
      },

      // 搜索表单
      searchForm: {
        keyword: '',
        timeRange: null,
        action: '',
        resource: '',
        result: '',
        riskLevel: '',
        clientType: ''
      },
      timeRangeType: 'today',
      showMoreFilters: false,
      currentQuickFilter: '',

      // 视图模式
      viewMode: 'list',

      // 列选项
      columnOptions: [
        { prop: 'operator', label: '操作用户', visible: true },
        { prop: 'resource', label: '资源类型', visible: true },
        { prop: 'result', label: '操作结果', visible: true },
        { prop: 'ip', label: 'IP地址', visible: true },
        { prop: 'client', label: '客户端', visible: true },
        { prop: 'time', label: '操作时间', visible: true }
      ],

      // 快速筛选
      quickFilters: [
        { label: '全部', value: '' },
        { label: '登录相关', value: 'login' },
        { label: '用户管理', value: 'user' },
        { label: '权限变更', value: 'permission' },
        { label: '失败操作', value: 'failed' },
        { label: '高风险', value: 'risk' }
      ],

      // 操作类型
      actionTypes: [
        { value: 'create', label: '创建', icon: 'el-icon-plus', color: '#67C23A' },
        { value: 'update', label: '修改', icon: 'el-icon-edit', color: '#409EFF' },
        { value: 'delete', label: '删除', icon: 'el-icon-delete', color: '#F56C6C' },
        { value: 'login', label: '登录', icon: 'el-icon-user', color: '#909399' },
        { value: 'logout', label: '登出', icon: 'el-icon-switch-button', color: '#909399' },
        { value: 'view', label: '查看', icon: 'el-icon-view', color: '#909399' },
        { value: 'export', label: '导出', icon: 'el-icon-download', color: '#E6A23C' },
        { value: 'import', label: '导入', icon: 'el-icon-upload2', color: '#E6A23C' },
        { value: 'enable', label: '启用', icon: 'el-icon-circle-check', color: '#67C23A' },
        { value: 'disable', label: '禁用', icon: 'el-icon-circle-close', color: '#F56C6C' },
        { value: 'assign', label: '分配', icon: 'el-icon-s-check', color: '#409EFF' },
        { value: 'revoke', label: '回收', icon: 'el-icon-remove', color: '#F56C6C' }
      ],

      // 资源类型
      resourceTypes: [
        { value: 'user', label: '用户', icon: 'el-icon-user', color: '#409EFF' },
        { value: 'userGroup', label: '用户组', icon: 'el-icon-s-custom', color: '#409EFF' },
        { value: 'role', label: '角色', icon: 'el-icon-medal', color: '#E6A23C' },
        { value: 'app', label: '应用', icon: 'el-icon-s-platform', color: '#67C23A' },
        { value: 'permission', label: '权限', icon: 'el-icon-key', color: '#F56C6C' },
        { value: 'policy', label: '策略', icon: 'el-icon-document', color: '#909399' },
        { value: 'identitySource', label: '身份源', icon: 'el-icon-share', color: '#409EFF' },
        { value: 'apiResource', label: 'API资源', icon: 'el-icon-cpu', color: '#67C23A' },
        { value: 'config', label: '配置', icon: 'el-icon-setting', color: '#909399' }
      ],

      // 表格数据
      tableData: [],
      currentPage: 1,
      pageSize: 20,
      total: 156,

      // 详情抽屉
      detailDrawerVisible: false,
      currentDetail: null,

      // 导出对话框
      exportDialogVisible: false,
      exportForm: {
        timeRange: [],
        action: [],
        resource: [],
        format: 'excel'
      },
      exporting: false,

      // 错误对话框
      errorDialogVisible: false,
      currentError: null
    }
  },

  created() {
    this.handleTimeRangeChange('today')
    this.loadData()
  },

  methods: {
    // 加载数据
    loadData() {
      this.loading = true
      // 模拟API调用
      setTimeout(() => {
        this.tableData = this.generateMockData()
        this.loading = false
      }, 500)
    },

    generateMockData() {
      const actions = ['create', 'update', 'delete', 'login', 'logout', 'view', 'assign']
      const resources = ['user', 'userGroup', 'role', 'app', 'permission', 'policy']
      const users = ['admin', 'zhangsan', 'lisi', 'wangwu', 'zhaoliu']
      const ips = ['192.168.1.100', '10.0.0.50', '172.16.0.25', '192.168.2.200']

      return Array.from({ length: 20 }, (_, i) => {
        const action = actions[Math.floor(Math.random() * actions.length)]
        const resource = resources[Math.floor(Math.random() * resources.length)]
        const resourceLabel = this.resourceTypes.find(r => r.value === resource)?.label || resource
        const result = Math.random() > 0.9 ? 'failed' : Math.random() > 0.95 ? 'warning' : 'success'
        const riskLevel = result === 'failed' && Math.random() > 0.5 ? 'high' : Math.random() > 0.9 ? 'medium' : 'low'

        return {
          id: `AUD${Date.now()}${i}`,
          title: `${this.getActionLabel(action)}${resourceLabel}`,
          description: `${this.getActionLabel(action)}了${resourceLabel}: ${this.getRandomResourceName(resource)}`,
          operator: users[Math.floor(Math.random() * users.length)],
          department: '技术部',
          userRole: '管理员',
          resource,
          resourceLabel,
          action,
          result,
          riskLevel,
          ip: ips[Math.floor(Math.random() * ips.length)],
          location: '北京市',
          clientType: ['web', 'mobile', 'api'][Math.floor(Math.random() * 3)],
          clientInfo: 'Chrome 120.0 / Windows 10',
          time: this.getRandomTime(),
          userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
          errorMessage: result === 'failed' ? '权限不足，无法执行此操作' : undefined,
          changes: this.generateChanges(action),
          rawData: { id: i, action, resource, timestamp: Date.now() }
        }
      })
    },

    getRandomResourceName(resource) {
      const names = {
        user: ['张三', '李四', '王五', '赵六'],
        userGroup: ['开发组', '测试组', '产品组'],
        role: ['管理员', '普通用户', '审计员'],
        app: ['管理系统', '门户系统', 'API网关'],
        permission: ['用户读取', '用户写入', '系统配置'],
        policy: ['密码策略', '登录策略', '访问策略']
      }
      const list = names[resource] || ['资源']
      return list[Math.floor(Math.random() * list.length)]
    },

    getRandomTime() {
      const now = new Date()
      const offset = Math.floor(Math.random() * 86400000)
      return new Date(now - offset).toISOString()
    },

    generateChanges(action) {
      if (action !== 'update') return []
      return [
        { field: '状态', oldValue: '启用', newValue: '禁用' },
        { field: '角色', oldValue: '普通用户', newValue: '管理员' }
      ]
    },

    // 时间范围处理
    handleTimeRangeChange(type) {
      const now = new Date()
      const end = new Date(now)
      const start = new Date(now)

      switch (type) {
        case 'today':
          start.setHours(0, 0, 0, 0)
          break
        case 'week':
          start.setDate(now.getDate() - 7)
          break
        case 'month':
          start.setMonth(now.getMonth() - 1)
          break
        case 'custom':
          return
      }

      this.searchForm.timeRange = [
        this.formatDateTime(start),
        this.formatDateTime(end)
      ]
    },

    formatDateTime(date) {
      const pad = n => n.toString().padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
    },

    // 搜索和筛选
    search() {
      this.currentPage = 1
      this.loadData()
    },

    reset() {
      this.searchForm = {
        keyword: '',
        timeRange: null,
        action: '',
        resource: '',
        result: '',
        riskLevel: '',
        clientType: ''
      }
      this.timeRangeType = 'today'
      this.currentQuickFilter = ''
      this.handleTimeRangeChange('today')
      this.search()
    },

    applyQuickFilter(filter) {
      this.currentQuickFilter = filter
      // 根据快速筛选设置搜索条件
      switch (filter) {
        case 'login':
          this.searchForm.action = 'login'
          break
        case 'user':
          this.searchForm.resource = 'user'
          break
        case 'permission':
          this.searchForm.resource = 'permission'
          break
        case 'failed':
          this.searchForm.result = 'failed'
          break
        case 'risk':
          this.searchForm.riskLevel = 'high'
          break
        default:
          this.searchForm.action = ''
          this.searchForm.resource = ''
          this.searchForm.result = ''
          this.searchForm.riskLevel = ''
      }
      this.search()
    },

    // 分页
    onSizeChange(size) {
      this.pageSize = size
      this.loadData()
    },

    onPageChange(page) {
      this.currentPage = page
      this.loadData()
    },

    // 详情展示
    showDetail(row) {
      this.currentDetail = row
      this.detailDrawerVisible = true
    },

    handleRowClick(row) {
      this.showDetail(row)
    },

    // 错误详情
    showError(row) {
      this.currentError = row
      this.errorDialogVisible = true
    },

    // 导出
    confirmExport() {
      this.exporting = true
      setTimeout(() => {
        this.exporting = false
        this.exportDialogVisible = false
        this.$message.success('日志导出成功')
      }, 1500)
    },

    exportSingle(row) {
      this.$message.success('单条记录导出成功')
    },

    // 工具方法
    getActionIcon(action) {
      const map = {
        create: 'el-icon-plus',
        update: 'el-icon-edit',
        delete: 'el-icon-delete',
        login: 'el-icon-user',
        logout: 'el-icon-switch-button',
        view: 'el-icon-view',
        export: 'el-icon-download',
        import: 'el-icon-upload2',
        enable: 'el-icon-circle-check',
        disable: 'el-icon-circle-close',
        assign: 'el-icon-s-check',
        revoke: 'el-icon-remove'
      }
      return map[action] || 'el-icon-question'
    },

    getActionColor(action) {
      const map = {
        create: '#67C23A',
        update: '#409EFF',
        delete: '#F56C6C',
        login: '#909399',
        logout: '#909399',
        view: '#909399',
        export: '#E6A23C',
        import: '#E6A23C',
        enable: '#67C23A',
        disable: '#F56C6C',
        assign: '#409EFF',
        revoke: '#F56C6C'
      }
      return map[action] || '#909399'
    },

    getActionLabel(action) {
      const item = this.actionTypes.find(a => a.value === action)
      return item ? item.label : action
    },

    getResourceIcon(resource) {
      const item = this.resourceTypes.find(r => r.value === resource)
      return item ? item.icon : 'el-icon-question'
    },

    getResultType(result) {
      const map = { success: 'success', failed: 'danger', warning: 'warning' }
      return map[result] || 'info'
    },

    getRiskType(level) {
      const map = { high: 'danger', medium: 'warning', low: 'success' }
      return map[level] || 'info'
    },

    getClientIcon(type) {
      const map = {
        web: 'el-icon-monitor',
        mobile: 'el-icon-mobile-phone',
        api: 'el-icon-cpu',
        cli: 'el-icon-s-platform'
      }
      return map[type] || 'el-icon-question'
    },

    getTimelineType(item) {
      if (item.result === 'failed') return 'danger'
      if (item.riskLevel === 'high') return 'warning'
      return 'primary'
    },

    getTimelineColor(item) {
      if (item.result === 'failed') return '#F56C6C'
      if (item.riskLevel === 'high') return '#E6A23C'
      return '#409EFF'
    },

    formatTime(time) {
      const date = new Date(time)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

      return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
    },

    formatFullTime(time) {
      return new Date(time).toLocaleString('zh-CN')
    },

    formatJson(data) {
      return JSON.stringify(data, null, 2)
    },

    copyRawData() {
      this.$copyText(this.formatJson(this.currentDetail.rawData)).then(() => {
        this.$message.success('已复制到剪贴板')
      })
    },

    // 刷新
    refreshData() {
      this.loadData()
      this.$message.success('数据已刷新')
    },

    // IP黑名单
    blockIP(ip) {
      this.$confirm(`确定将 IP ${ip} 加入黑名单吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success(`IP ${ip} 已加入黑名单`)
      })
    },

    // 加载更多
    loadMore() {
      this.loadingMore = true
      setTimeout(() => {
        this.loadingMore = false
        this.tableData.push(...this.generateMockData().slice(0, 10))
      }, 500)
    }
  }
}
</script>

<style lang="scss" scoped>
.audit-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;

  // 页面头部统计
  .page-header-section {
    margin-bottom: 20px;

    .page-title-area {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .page-title {
        font-size: 24px;
        font-weight: 600;
        margin: 0 0 8px 0;
        color: #303133;
      }

      .page-desc {
        color: #909399;
        font-size: 14px;
        margin: 0;
      }

      .page-actions {
        display: flex;
        gap: 10px;
      }
    }

    .stats-row {
      .stat-card {
        background: #fff;
        border-radius: 8px;
        padding: 16px;
        display: flex;
        align-items: center;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          font-size: 24px;

          &.total {
            background: #ecf5ff;
            color: #409eff;
          }
          &.success {
            background: #f0f9eb;
            color: #67c23a;
          }
          &.failed {
            background: #fef0f0;
            color: #f56c6c;
          }
          &.warning {
            background: #fdf6ec;
            color: #e6a23c;
          }
          &.login {
            background: #f4f4f5;
            color: #909399;
          }
          &.users {
            background: #f0f9eb;
            color: #67c23a;
          }
        }

        .stat-content {
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
            line-height: 1.2;
          }

          .stat-label {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }

  // 筛选卡片
  .filter-card {
    margin-bottom: 16px;

    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;

      i {
        margin-right: 6px;
      }
    }

    .more-filters {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid #ebeef5;
    }

    .quick-filters {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid #ebeef5;
      display: flex;
      align-items: center;
      gap: 10px;

      .quick-label {
        color: #909399;
        font-size: 13px;
      }

      .el-tag {
        cursor: pointer;
        transition: all 0.3s;

        &:hover, &.active {
          background-color: #ecf5ff;
          border-color: #409eff;
          color: #409eff;
        }
      }
    }
  }

  // 日志卡片
  .log-card {
    .log-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-left {
        display: flex;
        align-items: center;
        gap: 10px;
        font-weight: 600;
      }

      .header-right {
        display: flex;
        align-items: center;
        gap: 10px;

        .el-divider--vertical {
          margin: 0;
        }
      }
    }

    // 事件单元格
    .event-cell {
      display: flex;
      align-items: center;

      .event-icon {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        flex-shrink: 0;

        i {
          font-size: 18px;
        }
      }

      .event-content {
        min-width: 0;
        flex: 1;

        .event-title {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;

          .risk-tag {
            flex-shrink: 0;
          }
        }

        .event-desc {
          font-size: 12px;
          color: #909399;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }

    // 用户单元格
    .user-cell {
      display: flex;
      align-items: center;

      .user-avatar {
        margin-right: 8px;
      }

      .user-info {
        .user-name {
          font-weight: 500;
          color: #303133;
        }

        .user-dept {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    // 资源图标
    .resource-icon {
      margin-right: 4px;
    }

    // IP单元格
    .ip-cell {
      .ip-address {
        display: block;
        font-family: monospace;
        color: #606266;
      }

      .ip-location {
        display: block;
        font-size: 12px;
        color: #909399;
      }
    }

    // 客户端单元格
    .client-cell {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #606266;
    }

    // 时间轴视图
    .timeline-view {
      padding: 20px;

      .timeline-card {
        background: #fff;
        border: 1px solid #ebeef5;
        border-radius: 8px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        }

        .timeline-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;

          .timeline-title {
            font-weight: 600;
            color: #303133;
          }
        }

        .timeline-content {
          .timeline-info {
            display: flex;
            gap: 20px;
            margin-bottom: 8px;
            color: #606266;
            font-size: 13px;

            i {
              margin-right: 4px;
            }
          }

          .timeline-desc {
            color: #909399;
            font-size: 13px;
          }
        }
      }

      .load-more {
        text-align: center;
        margin-top: 20px;
      }
    }

    // 分页
    .pagination {
      margin-top: 16px;
      text-align: right;
    }
  }

  // 详情抽屉
  .detail-drawer {
    display: flex;
    flex-direction: column;
    height: 100%;

    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #ebeef5;

      .detail-title {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 18px;
        font-weight: 600;

        i {
          font-size: 24px;
        }
      }
    }

    .detail-body {
      flex: 1;
      overflow-y: auto;
      padding: 20px;

      .detail-section {
        margin-bottom: 24px;

        .section-title {
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .user-detail {
          display: flex;
          align-items: center;
          padding: 16px;
          background: #f5f7fa;
          border-radius: 8px;

          .user-detail-info {
            margin-left: 16px;

            .detail-user-name {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
            }

            .detail-user-meta {
              margin-top: 8px;
              color: #909399;
              font-size: 13px;

              span {
                margin-right: 16px;
              }

              i {
                margin-right: 4px;
              }
            }
          }
        }

        .changes-list {
          .change-item {
            background: #f5f7fa;
            border-radius: 8px;
            padding: 12px 16px;
            margin-bottom: 10px;

            .change-field {
              font-weight: 600;
              color: #303133;
              margin-bottom: 8px;
            }

            .change-values {
              display: flex;
              align-items: center;
              gap: 12px;

              .change-old, .change-new {
                flex: 1;

                .change-label {
                  font-size: 12px;
                  color: #909399;
                  margin-right: 6px;
                }

                .value {
                  padding: 4px 8px;
                  border-radius: 4px;
                  font-family: monospace;

                  &.old {
                    background: #fef0f0;
                    color: #f56c6c;
                    text-decoration: line-through;
                  }

                  &.new {
                    background: #f0f9eb;
                    color: #67c23a;
                  }
                }
              }

              .change-arrow {
                color: #909399;
              }
            }
          }
        }

        .raw-json {
          background: #f7f7f7;
          padding: 12px;
          border-radius: 6px;
          font-family: 'Courier New', monospace;
          font-size: 12px;
          overflow-x: auto;
          max-height: 300px;
          overflow-y: auto;
        }

        .user-agent-text {
          max-width: 400px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          display: inline-block;
          vertical-align: bottom;
        }

        .error-reason {
          margin-left: 10px;
          color: #f56c6c;
        }
      }
    }

    .detail-footer {
      padding: 16px 20px;
      border-top: 1px solid #ebeef5;
      display: flex;
      justify-content: flex-end;
      gap: 10px;
    }
  }

  // 错误详情
  .error-detail {
    text-align: center;
    padding: 20px;

    .error-icon {
      font-size: 64px;
      color: #f56c6c;
      margin-bottom: 16px;
    }

    .error-message {
      font-size: 16px;
      color: #f56c6c;
      margin-bottom: 20px;
    }

    .error-stack {
      text-align: left;
      background: #f7f7f7;
      padding: 12px;
      border-radius: 6px;

      pre {
        margin: 0;
        font-size: 12px;
        color: #606266;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }
  }
}
</style>
