<template>
  <div class="identity-source-page">

    <!-- 顶部标题 + 搜索框 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">企业身份源管理</h2>
        <p class="page-desc">管理 LDAP、Active Directory、OIDC、SAML 等企业身份源。</p>
      </div>

      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索身份源名称"
          prefix-icon="el-icon-search"
          clearable
        ></el-input>
      </div>
    </div>

    <!-- 白色卡片列表 -->
    <el-button
      type="primary"
      icon="el-icon-plus"
      class="btn-create"
      @click="goCreate"
    >
      新增身份源
    </el-button>
    <div class="table-container">

      <div class="table-header">

      </div>

      <el-table
        :data="filteredList"
        border
        class="list-table"
      >
        <el-table-column label="名称" min-width="180">
          <template slot-scope="scope">
            <div class="name-col">
              <img :src="scope.row.icon" class="source-icon" />
              {{ scope.row.name }}
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="120"></el-table-column>

        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'connected' ? 'success' : 'danger'">
              {{ scope.row.status === 'connected' ? '已连接' : '未连接' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="users" label="同步用户数" width="140"></el-table-column>

        <el-table-column prop="updateTime" label="更新时间" width="180"></el-table-column>

        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="edit(scope.row)">编辑</el-button>
            <el-button type="text" @click="sync(scope.row)">同步</el-button>
            <el-button type="text" style="color:red" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>

      </el-table>

      <!-- 分页 -->
      <div class="pager">
        <el-pagination
          layout="prev, pager, next"
          :total="filteredList.length"
          :page-size="10"
        ></el-pagination>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "IdentitySourcePage",

  data() {
    return {
      keyword: "",

      list: [
        {
          id: 1,
          name: "公司 LDAP",
          type: "LDAP",
          status: "connected",
          users: 324,
          updateTime: "2025-02-12 14:22",
          // icon: require("@/assets/icons/ldap.png"),
        },
        {
          id: 2,
          name: "总部 AD",
          type: "Active Directory",
          status: "connected",
          users: 1124,
          updateTime: "2025-02-12 13:10",
          // icon: require("@/assets/icons/ad.png"),
        },
        {
          id: 3,
          name: "Ping Identity",
          type: "OIDC",
          status: "disconnected",
          users: 0,
          updateTime: "2025-02-01 09:15",
          // icon: require("@/assets/icons/oidc.png"),
        },
        {
          id: 4,
          name: "企业 SSO",
          type: "SAML",
          status: "connected",
          users: 234,
          updateTime: "2025-02-10 09:30",
          // icon: require("@/assets/icons/saml.png"),
        }
      ]
    };
  },

  computed: {
    filteredList() {
      return this.list.filter(item =>
        !this.keyword || item.name.includes(this.keyword)
      );
    }
  },

  methods: {
    goCreate() {
      this.$router.push("/identity/create");
    },
    edit(row) {
      this.$message("编辑：" + row.name);
    },
    sync(row) {
      this.$message.success("已启动同步：" + row.name);
    },
    remove(row) {
      this.$confirm("确定删除此身份源吗？", "提示").then(() => {
        this.$message.success("已删除：" + row.name);
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.identity-source-page {
  padding: 30px 40px;

  .page-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 18px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
  }

  .page-desc {
    color: #666;
    margin-top: 4px;
  }

  .search-box {
    width: 280px;
  }

  .table-container {
    background: #fff;
    border-radius: 10px;
    padding: 20px 20px 10px;
    border: 1px solid #eee;

    .table-header {
      display: flex;
      justify-content: flex-end;
      margin-bottom: 10px;

      .btn-create {
        border: none;
        border-radius: 6px;
      }
    }
  }

  .name-col {
    display: flex;
    align-items: center;

    .source-icon {
      width: 26px;
      margin-right: 10px;
    }
  }

  .pager {
    display: flex;
    justify-content: center;
    padding: 16px 0;
  }
}
</style>
