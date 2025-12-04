<template>
  <div class="user-page">

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" size="small" class="search-form">

        <!-- 常规搜索 -->
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable/>
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable/>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable/>
        </el-form-item>

        <div class="search-btn-group">
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button type="text" class="toggle-advanced-btn" @click="toggleAdvanced">
            <i :class="showAdvanced ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            高级搜索
          </el-button>
        </div>

        <!-- 折叠高级搜索 -->
        <transition name="slide-fade">
          <div v-show="showAdvanced" class="advanced-search">
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="searchForm.createTime"
                type="daterange"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                unlink-panels
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </div>
        </transition>

      </el-form>
    </el-card>

    <!-- 列表卡片 -->
    <el-card class="table-card" shadow="hover">

      <!-- 顶部操作行 -->
      <div class="table-header-bar">
        <el-button
          size="small"
          type="primary"
          icon="el-icon-plus"
          class="add-btn"
          @click="addUser"
        >
          新增用户
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        class="user-table"
        :header-cell-style="tableHeaderStyle"
      >
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="username" label="用户名"/>
        <el-table-column prop="phone" label="手机号" width="140"/>
        <el-table-column prop="email" label="邮箱"/>
        <el-table-column prop="createTime" label="创建时间" width="150"/>
        <el-table-column label="操作" width="180">
          <template slot-scope="{ row }">
            <el-button size="mini" class="text-btn" @click="editUser(row)">编辑</el-button>
            <el-button size="mini" class="text-btn danger" @click="deleteUser(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          background
          layout="prev, pager, next, jumper"
          :page-size="pageSize"
          :total="total"
          @current-change="pageChange"
        />
      </div>

    </el-card>

  </div>
</template>

<script>
export default {
  name: "UserList",
  data() {
    return {
      showAdvanced: false,
      searchForm: {
        username: "",
        phone: "",
        email: "",
        createTime: null
      },
      tableData: [
        { id: 1, username: "zhangsan", phone: "13800000001", email: "zhangsan@example.com", createTime: "2023-04-10" },
        { id: 2, username: "lisi", phone: "13800000002", email: "lisi@example.com", createTime: "2023-05-01" }
      ],
      pageSize: 10,
      total: 52
    };
  },
  methods: {
    toggleAdvanced() {
      this.showAdvanced = !this.showAdvanced;
    },
    search() {
      this.$message.success("查询成功");
    },
    reset() {
      this.searchForm = { username: "", phone: "", email: "", createTime: null };
    },
    addUser() {
      this.$message.info("点击新增用户");
    },
    editUser(row) {
      this.$message.info("编辑：" + row.username);
    },
    deleteUser(row) {
      this.$confirm(`确认删除用户：${row.username}?`, "提示", { type: "warning" })
        .then(() => this.$message.success("已删除"))
        .catch(() => {});
    },
    pageChange(p) {
      this.$message.info(`跳转到第 ${p} 页`);
    },
    tableHeaderStyle() {
      return { background: "#f5f7fa", fontWeight: "bold", color: "#303133" };
    }
  }
};
</script>

<style lang="scss" scoped>
.user-page {
  padding: 20px;

  .search-card {
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .search-form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .el-form-item {
      margin-right: 20px;
      margin-bottom: 12px;
    }

    .search-btn-group {
      display: flex;
      align-items: center;
      margin-left: auto;

      .toggle-advanced-btn {
        color: #409eff;
        display: flex;
        align-items: center;

        i {
          margin-right: 4px;
        }
      }
    }

    .advanced-search {
      margin-top: 12px;
      display: flex;
      flex-wrap: wrap;

      .el-form-item {
        margin-right: 20px;
      }
    }
  }

  .table-card {
    border-radius: 10px;
    padding-bottom: 10px;

    .table-header-bar {
      display: flex;
      justify-content: flex-start;
      margin-bottom: 12px;

      .add-btn {
        border: none !important;
        background-color: #409eff20;
        color: #409eff;
        font-weight: 500;
      }

      .add-btn:hover {
        background-color: #409eff30;
      }
    }

    .user-table {
      width: 100%;
      border: none;

      ::v-deep .el-table__body-wrapper {
        border-radius: 6px;
      }

      ::v-deep .el-table--enable-row-hover tr:hover > td {
        background-color: #f5f7fa !important;
      }

      .text-btn {
        border: none;
        background: transparent;
        color: #409eff;
        padding: 0 6px;
      }

      .text-btn:hover {
        color: #66b1ff;
        text-decoration: underline;
      }

      .text-btn.danger {
        color: #f56c6c;
      }

      .text-btn.danger:hover {
        color: #f78989;
      }
    }

    .pagination-box {
      margin-top: 15px;
      text-align: right;
    }
  }
}

/* 高级搜索下拉动画 */
.slide-fade-enter-active, .slide-fade-leave-active {
  transition: all 0.3s ease;
}
.slide-fade-enter, .slide-fade-leave-to {
  opacity: 0;
  max-height: 0;
  overflow: hidden;
}
</style>
