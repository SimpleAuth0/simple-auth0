<template>
  <div class="app-create-page">

    <!-- 顶部标题 -->
    <h2 class="page-title">创建新应用程序</h2>

    <!-- 搜索框 -->
    <div class="search-box">
      <el-input
        v-model="keyword"
        placeholder="按名称搜索"
        prefix-icon="el-icon-search"
        clearable
      />
    </div>

    <!-- 协议过滤标签 -->
    <div class="protocol-tags">
      <el-tag
        v-for="item in protocolTags"
        :key="item"
        :type="activeTag === item ? '' : 'info'"
        class="tag-item"
        @click="selectTag(item)"
      >
        {{ item }}
      </el-tag>
    </div>

    <!-- 应用类型标题 -->
    <h3 class="section-title">选择应用程序类型</h3>
    <p class="page-desc">
      请使用以下模板之一创建应用程序。如果没有符合您应用程序类型的模板，请从“基于标准的应用程序”模板开始。
    </p>

    <!-- 应用类型卡片区域 -->
    <div class="app-card-grid">

      <div
        class="app-card"
        v-for="item in filteredApps"
        :key="item.id"
        @click="selectApp(item)"
      >
        <!-- 图标 + 标题（横向） -->
        <div class="card-header">
          <img class="card-icon" :src="item.icon"/>
          <div class="card-title">{{ item.name }}</div>
        </div>

        <!-- 描述 -->
        <div class="card-desc">
          {{ item.desc }}
        </div>

        <!-- 技术图标 -->
        <div class="card-tech">
          <img v-for="t in item.tech" :src="t" :key="t"/>
        </div>

        <!-- NEW 标签 -->
        <div class="badge-new" v-if="item.new">新</div>
      </div>

    </div>

  </div>
</template>


<script>
export default {
  name: "AppCreatePage",

  data() {
    return {
      keyword: "",
      activeTag: "全部",

      protocolTags: ["全部", "OIDC", "CAS", "SAML", "OAuth2"],

      apps: [
        {
          id: 1,
          name: "单页应用程序",
          desc: "在浏览器中运行应用程序逻辑的 Web 应用程序。",
          icon: require("@/assets/img/spa.svg"),
          tech: [
            require("@/assets/img/react.svg"),
            require("@/assets/img/vuejs.svg"),
            require("@/assets/img/javascript.svg")
          ],
          proto: ["OIDC"],
        },
        {
          id: 2,
          name: "传统 Web 应用程序",
          desc: "在服务器端运行应用程序逻辑的 Web 应用程序。",
          icon: require("@/assets/img/web.svg"),
          tech: [
            require("@/assets/img/java.svg"),
            require("@/assets/img/python.svg"),
            require("@/assets/img/go.svg")
          ],
          proto: ["OIDC", "SAML"],
        },
        {
          id: 3,
          name: "移动应用",
          desc: "专为移动设备开发的应用程序。",
          icon: require("@/assets/img/mobile.svg"),
          tech: [
            require("@/assets/img/android.svg"),
            require("@/assets/img/ios.png"),
            require("@/assets/img/flutter.svg")
          ],
          proto: ["OIDC"],
        },
        {
          id: 4,
          name: "M2M 应用",
          desc: "专为机器间通信而设计的应用。",
          icon: require("@/assets/img/m2m.svg"),
          tech: [
            require("@/assets/img/oauth.svg"),
          ],
          proto: ["OAuth2"],
        },
        {
          id: 5,
          name: "基于标准的应用程序",
          desc: "使用标准协议构建的应用。",
          icon: require("@/assets/img/std.svg"),
          tech: [
            require("@/assets/img/oauth.svg"),
            require("@/assets/img/openid.png"),
            require("@/assets/img/saml.svg"),
            require("@/assets/img/cas.png"),
          ],
          proto: ["SAML", "OIDC", "CAS", "OAuth2"],
        },
      ],
    };
  },

  computed: {
    filteredApps() {
      return this.apps.filter(app => {
        const matchKeyword = !this.keyword || app.name.includes(this.keyword);

        const matchTag =
          this.activeTag === "全部" || app.proto.includes(this.activeTag);

        return matchKeyword && matchTag;
      });
    }
  },

  methods: {
    selectTag(tag) {
      this.activeTag = tag;
    },

    selectApp(item) {
      this.$message.success("选择了：" + item.name);
    }
  }
};
</script>

<style lang="scss" scoped>
.app-create-page {
  padding: 30px 40px;
  color: #333;

  .page-title {
    font-size: 26px;
    font-weight: 600;
  }

  .page-desc {
    color: #666;
    margin-bottom: 25px;
  }

  .search-box {
    width: 400px;
    margin-bottom: 20px;
  }

  .protocol-tags {
    margin-bottom: 25px;

    .tag-item {
      margin-right: 10px;
      cursor: pointer;
      border-radius: 20px;
      padding: 0px 14px;
    }
  }

  .section-title {
    font-weight: 600;
    margin-top: 10px;
  }

  .app-card-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 22px;
  }

  .app-card {
    position: relative;
    border: 1px solid #e5e5e5;
    border-radius: 14px;
    padding: 26px;
    background: #fff;
    cursor: pointer;
    transition: all 0.22s;
    min-height: 180px;

    &:hover {
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
      transform: translateY(-3px);
    }

    /* 图标 + 标题（横向居左） */
    .card-header {
      display: flex;
      align-items: center;
      margin-bottom: 12px;

      .card-icon {
        width: 40px;
        height: 40px;
        margin-right: 14px;
      }

      .card-title {
        font-size: 18px;
        font-weight: 600;
      }
    }

    /* 描述文字 */
    .card-desc {
      color: #666;
      font-size: 14px;
      line-height: 20px;
      margin-bottom: 16px;
      min-height: 40px;
    }

    /* 技术图标 */
    .card-tech {
      img {
        width: 28px;
        margin-right: 6px;
      }
    }

    .badge-new {
      position: absolute;
      right: 12px;
      top: 12px;
      background: #4caf50;
      color: #fff;
      font-size: 12px;
      padding: 2px 8px;
      border-radius: 10px;
    }
  }
}
</style>

