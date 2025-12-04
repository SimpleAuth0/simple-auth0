<template>
  <div class="app-page">

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" size="small">

        <el-form-item label="应用名称">
          <el-input v-model="query.name" placeholder="输入名称或 ClientId"/>
        </el-form-item>

        <el-form-item label="类型">
          <el-select v-model="query.type" clearable placeholder="全部类型">
            <el-option label="Web" value="web"/>
            <el-option label="SPA" value="spa"/>
            <el-option label="Native" value="native"/>
            <el-option label="Machine-to-Machine" value="m2m"/>
            <el-option label="SAML" value="saml"/>
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部">
            <el-option label="启用" value="enabled"/>
            <el-option label="停用" value="disabled"/>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
        </el-form-item>

        <el-form-item style="float:right">
          <el-button type="primary" icon="el-icon-plus" @click="openCreateDialog">
            新增应用
          </el-button>
        </el-form-item>

      </el-form>
    </el-card>

    <!-- 应用列表 -->
    <el-card class="table-card">
      <div class="card-title">应用列表</div>

      <el-table :data="appList" class="app-table" highlight-current-row>
        <el-table-column label="应用名称" prop="name" min-width="150"/>
        <el-table-column label="Client ID" prop="clientId" min-width="200"/>

        <el-table-column label="类型" prop="type" width="160">
          <template slot-scope="{ row }">
            <el-tag size="mini">{{ row.type }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="鉴权方式" prop="authMethod" width="160"/>

        <el-table-column label="回调地址" min-width="200">
          <template slot-scope="{ row }">
            <div v-if="row.redirectUris && row.redirectUris.length">
              {{ row.redirectUris[0] }}
              <span v-if="row.redirectUris.length > 1" class="more">
                +{{ row.redirectUris.length - 1 }} 更多
              </span>
            </div>
            <div v-else class="empty">无</div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="mini">
              {{ row.enabled ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="editApp(row)">编辑</el-button>
            <el-button size="mini" type="text" @click="resetSecret(row)">重置密钥</el-button>
            <el-button size="mini" type="text" class="danger" @click="deleteApp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :page-size="10"
          :total="total"
        />
      </div>
    </el-card>

  </div>
</template>


<script>
export default {
  name: "ApplicationList",

  data() {
    return {
      query: {
        name: "",
        type: "",
        status: ""
      },

      appList: [
        {
          name: "后台管理系统",
          clientId: "abcd-1234-efgh",
          type: "Web",
          authMethod: "OIDC",
          redirectUris: ["https://admin.example.com/callback"],
          enabled: true
        },
        {
          name: "自动化脚本",
          clientId: "xxxx-8888-0000",
          type: "Machine-to-Machine",
          authMethod: "Client Credentials",
          redirectUris: [],
          enabled: false
        }
      ],

      total: 2
    };
  },

  methods: {
    search() {
      this.$message.success("已更新搜索结果");
    },

    openCreateDialog() {
      this.$router.push("/app/create");
    },

    editApp(app) {
      this.$message.info("打开编辑应用：" + app.name);
    },

    resetSecret(app) {
      this.$confirm(`确定重置应用密钥？`, "提示", { type: "warning" })
        .then(() => this.$message.success("密钥已重置"))
        .catch(()=>{});
    },

    deleteApp(app) {
      this.$confirm(`确定删除应用：${app.name}？`, "提示", { type: "warning" })
        .then(() => this.$message.success("已删除"))
        .catch(()=>{});
    }
  }
};
</script>


<style scoped lang="scss">
.app-page {
  padding: 20px;
}

/* 搜索栏样式 */
.search-card {
  margin-bottom: 20px;
  border-radius: 10px;
}

/* 表格卡片 */
.table-card {
  border-radius: 10px;

  .card-title {
    font-size: 18px;
    font-weight: 600;
    padding-bottom: 12px;
  }
}

.app-table .more {
  color: #409eff;
  font-size: 12px;
  margin-left: 5px;
}
.empty {
  color: #999;
  font-size: 12px;
}
.danger {
  color: #f56c6c !important;
}

.pagination {
  text-align: right;
  margin-top: 15px;
}
</style>
