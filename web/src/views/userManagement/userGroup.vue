<template>
  <div class="group-page">

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" size="small" class="search-form">

        <!-- 常规搜索 -->
        <el-form-item label="用户组名称">
          <el-input v-model="searchForm.groupName" placeholder="请输入用户组名称" clearable/>
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="searchForm.description" placeholder="请输入描述" clearable/>
        </el-form-item>

        <div class="search-btn-group">
          <el-button type="primary" icon="el-icon-search" size="mini" @click="search">查询</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
          <el-button size="mini" type="text" class="toggle-advanced-btn" @click="toggleAdvanced">
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
          @click="addGroup"
        >
          新增用户组
        </el-button>
        <el-button
          size="small"
          type="danger"
          icon="el-icon-delete"
          plain
          :disabled="selectedRows.length === 0"
          @click="batchDelete"
        >
          批量删除
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        class="group-table"
        :header-cell-style="tableHeaderStyle"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"/>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="groupName" label="用户组名称" min-width="150">
          <template slot-scope="{ row }">
            <div class="group-name-cell">
              <el-avatar :size="32" icon="el-icon-user-solid" class="group-avatar"/>
              <span class="group-name">{{ row.groupName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="memberCount" label="成员数" width="100">
          <template slot-scope="{ row }">
            <el-button type="text" @click="viewMembers(row)">{{ row.memberCount }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"/>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="editGroup(row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-user" @click="manageMembers(row)">成员管理</el-button>
            <el-popconfirm
              title="确定删除该用户组吗？"
              @confirm="deleteGroup(row)"
            >
              <el-button slot="reference" size="mini" type="text" class="danger" icon="el-icon-delete">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :total="total"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>

    </el-card>

    <!-- 新增/编辑用户组对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="550px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="groupForm"
        :model="groupForm"
        :rules="groupRules"
        label-width="100px"
        class="group-form"
      >
        <el-form-item label="组名称" prop="groupName">
          <el-input v-model="groupForm.groupName" placeholder="请输入用户组名称" maxlength="50" show-word-limit/>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="groupForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入用户组描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 成员管理对话框 -->
    <el-dialog
      title="成员管理"
      :visible.sync="memberDialogVisible"
      width="700px"
    >
      <div class="member-manage">
        <div class="member-transfer">
          <el-transfer
            v-model="selectedMembers"
            :data="allUsers"
            :titles="['未分配用户', '已分配用户']"
            :button-texts="['移除', '添加']"
            filterable
            :filter-method="filterUser"
            filter-placeholder="搜索用户名"
          />
        </div>
      </div>
      <div slot="footer">
        <el-button @click="memberDialogVisible = false">关 闭</el-button>
        <el-button type="primary" :loading="memberLoading" @click="saveMembers">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 查看成员对话框 -->
    <el-dialog
      title="组成员列表"
      :visible.sync="memberListVisible"
      width="600px"
    >
      <el-table :data="currentMembers" size="small">
        <el-table-column prop="username" label="用户名"/>
        <el-table-column prop="nickname" label="昵称"/>
        <el-table-column prop="email" label="邮箱"/>
      </el-table>
      <div slot="footer">
        <el-button @click="memberListVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: "GroupList",
  data() {
    return {
      loading: false,
      showAdvanced: false,
      searchForm: {
        groupName: "",
        description: "",
        createTime: null
      },
      tableData: [
        { id: 1, groupName: "管理员组", description: "系统管理员组，拥有全部权限", memberCount: 5, createTime: "2023-04-10 10:00:00" },
        { id: 2, groupName: "研发组", description: "开发人员组", memberCount: 12, createTime: "2023-05-01 09:00:00" },
        { id: 3, groupName: "测试组", description: "测试人员组", memberCount: 6, createTime: "2023-06-15 11:30:00" },
        { id: 4, groupName: "运维组", description: "运维人员组", memberCount: 4, createTime: "2023-07-20 14:00:00" }
      ],
      pageSize: 10,
      currentPage: 1,
      total: 4,
      selectedRows: [],

      // 对话框相关
      dialogVisible: false,
      dialogTitle: '新增用户组',
      isEdit: false,
      submitLoading: false,
      currentId: null,

      // 用户组表单
      groupForm: {
        groupName: '',
        description: ''
      },
      groupRules: {
        groupName: [
          { required: true, message: '请输入用户组名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
        ]
      },

      // 成员管理相关
      memberDialogVisible: false,
      memberLoading: false,
      currentGroupId: null,
      selectedMembers: [],
      allUsers: [
        { key: 1, label: 'admin', disabled: false },
        { key: 2, label: 'zhangsan', disabled: false },
        { key: 3, label: 'lisi', disabled: false },
        { key: 4, label: 'wangwu', disabled: false },
        { key: 5, label: 'zhaoliu', disabled: false }
      ],

      // 查看成员
      memberListVisible: false,
      currentMembers: []
    };
  },
  methods: {
    toggleAdvanced() {
      this.showAdvanced = !this.showAdvanced;
    },
    search() {
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        this.$message.success("查询成功");
      }, 500);
    },
    reset() {
      this.searchForm = { groupName: "", description: "", createTime: null };
      this.search();
    },
    addGroup() {
      this.dialogTitle = '新增用户组';
      this.isEdit = false;
      this.currentId = null;
      this.resetForm();
      this.dialogVisible = true;
    },
    editGroup(row) {
      this.dialogTitle = '编辑用户组';
      this.isEdit = true;
      this.currentId = row.id;
      this.groupForm = {
        groupName: row.groupName,
        description: row.description || ''
      };
      this.dialogVisible = true;
    },
    deleteGroup(row) {
      const index = this.tableData.findIndex(item => item.id === row.id);
      if (index > -1) {
        this.tableData.splice(index, 1);
        this.total--;
        this.$message.success('删除成功');
      }
    },
    batchDelete() {
      this.$confirm(`确定删除选中的 ${this.selectedRows.length} 个用户组吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        const ids = this.selectedRows.map(row => row.id);
        this.tableData = this.tableData.filter(item => !ids.includes(item.id));
        this.total -= ids.length;
        this.selectedRows = [];
        this.$message.success('批量删除成功');
      }).catch(() => {});
    },
    viewMembers(row) {
      // 模拟获取成员列表
      this.currentMembers = [
        { username: 'admin', nickname: '管理员', email: 'admin@example.com' },
        { username: 'zhangsan', nickname: '张三', email: 'zhangsan@example.com' }
      ];
      this.memberListVisible = true;
    },
    manageMembers(row) {
      this.currentGroupId = row.id;
      // 模拟已选择的成员
      this.selectedMembers = [1, 2];
      this.memberDialogVisible = true;
    },
    filterUser(query, item) {
      return item.label.indexOf(query) > -1;
    },
    saveMembers() {
      this.memberLoading = true;
      setTimeout(() => {
        this.memberLoading = false;
        this.memberDialogVisible = false;
        this.$message.success('成员分配成功');
      }, 800);
    },
    pageChange(p) {
      this.currentPage = p;
      this.search();
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.search();
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    tableHeaderStyle() {
      return { background: "#f5f7fa", fontWeight: "bold", color: "#303133" };
    },
    resetForm() {
      this.groupForm = {
        groupName: '',
        description: ''
      };
      this.$nextTick(() => {
        this.$refs.groupForm && this.$refs.groupForm.clearValidate();
      });
    },
    handleDialogClose() {
      this.resetForm();
    },
    submitForm() {
      this.$refs.groupForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          setTimeout(() => {
            this.submitLoading = false;
            if (this.isEdit) {
              const index = this.tableData.findIndex(item => item.id === this.currentId);
              if (index > -1) {
                this.tableData[index] = {
                  ...this.tableData[index],
                  ...this.groupForm
                };
              }
              this.$message.success('修改成功');
            } else {
              const newGroup = {
                id: this.tableData.length + 1,
                ...this.groupForm,
                memberCount: 0,
                createTime: new Date().toLocaleString()
              };
              this.tableData.unshift(newGroup);
              this.total++;
              this.$message.success('新增成功');
            }
            this.dialogVisible = false;
          }, 800);
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.group-page {
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
      width: 100%;

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
      gap: 10px;
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

    .group-table {
      width: 100%;
      border: none;

      ::v-deep .el-table__body-wrapper {
        border-radius: 6px;
      }

      ::v-deep .el-table--enable-row-hover tr:hover > td {
        background-color: #f5f7fa !important;
      }

      .group-name-cell {
        display: flex;
        align-items: center;

        .group-avatar {
          margin-right: 10px;
          background: #67c23a;
        }

        .group-name {
          font-weight: 500;
        }
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

      .danger {
        color: #f56c6c;
      }

      .danger:hover {
        color: #f78989;
      }
    }

    .pagination-box {
      margin-top: 15px;
      text-align: right;
    }
  }
}

.member-manage {
  .member-transfer {
    display: flex;
    justify-content: center;

    ::v-deep .el-transfer {
      .el-transfer-panel {
        width: 250px;
      }
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
