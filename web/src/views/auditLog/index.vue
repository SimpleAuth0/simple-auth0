<template>
  <div class="audit-page">

    <!-- 搜索卡片 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" size="small" label-width="80px">

        <el-form-item label="操作用户">
          <el-input v-model="searchForm.operator" clearable placeholder="请输入用户名称"/>
        </el-form-item>

        <el-form-item label="资源类型">
          <el-select v-model="searchForm.resource" clearable placeholder="请选择">
            <el-option label="用户" value="user"/>
            <el-option label="应用" value="app"/>
            <el-option label="策略" value="policy"/>
          </el-select>
        </el-form-item>

        <el-form-item label="操作类型">
          <el-select v-model="searchForm.action" clearable placeholder="请选择">
            <el-option label="创建" value="create"/>
            <el-option label="修改" value="update"/>
            <el-option label="删除" value="delete"/>
            <el-option label="登录" value="login"/>
          </el-select>
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button type="success" plain @click="exportData">导出日志</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="tableData" border stripe>

        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="operator" label="操作用户" width="120"/>
        <el-table-column prop="resource" label="资源类型" width="120"/>
        <el-table-column prop="action" label="操作类型" width="120"/>

        <el-table-column prop="ip" label="IP地址" width="140" />

        <el-table-column prop="result" label="执行结果" width="100">
          <template slot-scope="{ row }">
            <el-tag v-if="row.result === 'success'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="time" label="操作时间" width="160"/>

        <el-table-column label="详情" width="100">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="showDetail(row)">查看</el-button>
          </template>
        </el-table-column>

      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next, sizes, total"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          :current-page="currentPage"
          :total="total"
          @size-change="onSizeChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <!-- 日志详情弹窗 -->
    <el-dialog title="操作详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作用户">{{ detail.operator }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ detail.resource }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detail.action }}</el-descriptions-item>
        <el-descriptions-item label="IP 地址">{{ detail.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detail.time }}</el-descriptions-item>
        <el-descriptions-item label="结果">
          <el-tag :type="detail.result === 'success' ? 'success':'danger'">
            {{ detail.result === 'success' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="详情内容">
          <pre class="detail-json">{{ detail.data }}</pre>
        </el-descriptions-item>
      </el-descriptions>

      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: "AuditLog",

  data() {
    return {
      searchForm: {
        operator: "",
        resource: "",
        action: "",
        timeRange: null
      },

      tableData: [
        {
          id: 1,
          operator: "admin",
          resource: "用户",
          action: "创建",
          result: "success",
          ip: "10.1.1.12",
          time: "2025-01-10 10:20:00",
          data: '{ "username": "test", "role": "user" }'
        },
        {
          id: 2,
          operator: "alice",
          resource: "应用",
          action: "删除",
          result: "failed",
          ip: "192.168.1.88",
          time: "2025-01-10 11:00:00",
          data: '{ "appId": 1234, "reason": "permission denied" }'
        }
      ],

      currentPage: 1,
      pageSize: 10,
      total: 20,

      detailVisible: false,
      detail: {}
    };
  },

  methods: {
    search() {
      this.$message.success("筛选条件已应用（示例）");
    },

    reset() {
      this.searchForm = {
        operator: "",
        resource: "",
        action: "",
        timeRange: null
      };
    },

    onSizeChange(size) {
      this.pageSize = size;
    },

    onPageChange(page) {
      this.currentPage = page;
    },

    exportData() {
      this.$message.success("日志导出成功（示例）");
    },

    showDetail(row) {
      this.detail = row;
      this.detailVisible = true;
    }
  }
};
</script>

<style lang="scss" scoped>
.audit-page {
  padding: 20px;

  .search-card {
    margin-bottom: 16px;
  }

  .pagination {
    margin-top: 16px;
    text-align: right;
  }

  .detail-json {
    background: #f7f7f7;
    padding: 10px;
    border-radius: 6px;
    font-family: Menlo, monospace;
    white-space: pre-wrap;
  }
}
</style>
