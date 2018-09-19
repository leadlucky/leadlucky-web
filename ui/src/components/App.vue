<template>
  <body :class="`fix-header fix-sidebar ${portalData.miniSidebar ? 'mini-sidebar' : ''}`">
  <link href="/static/css/style.css" rel="stylesheet">
  <div id="main-wrapper">
    <div class="header is_stuck" style="position: fixed; top: 0px; width: 2560px;">
      <nav class="navbar top-navbar navbar-expand-md navbar-light" style="padding-right: 30px">
        <!-- Logo -->
        <div class="navbar-header">
          <a class="navbar-brand" href="index.html">
          </a>
        </div>
        <!-- End Logo -->
        <div class="navbar-collapse">
          <!-- toggle and nav items -->
          <ul class="navbar-nav mr-0 mt-md-0">
            <li @click="portalData.miniSidebar = !portalData.miniSidebar" class="nav-item m-l-10">
              <a class="nav-link sidebartoggler hidden-sm-down text-muted ">
                <i class="ti-menu"/>
              </a>
            </li>
          </ul>
          <ul class="navbar-nav mr-auto mt-md-0" style="margin-right: 1000px; margin-left: auto">
            <li class="nav-item dropdown m-l-10" >
              <a class="dropdown-toggle" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="ti-user" style="margin-right: 10px"/>{{currentUsername}} <i class="fa fa-caret-down"/>
              </a>
              <div class="dropdown-menu dropdown-menu-right animated slide-down">
                <ul class="dropdown-user">
                  <li><a href="#"><i class="ti-user"></i> Profile</a></li>
                  <li @click="logout"><a href="#"><i class="fa fa-power-off"></i> Logout</a></li>
                </ul>
              </div>
            </li>
          </ul>

        </div>
      </nav>
    </div>
    <div
      style="position: relative; width: 2560px; height: 56px; display: block; vertical-align: baseline; float: none;"></div>
    <SideNav/>
    <div class="page-wrapper" style="min-height: 733px; padding-bottom: 0">
      <router-view></router-view>
    </div>
  </div>
  </body>
</template>


<script>
  import alertmessage from './alertmessage'
  import PageTable from './PageTable'
  import PageTemplates from './PageTemplates'
  import auth from '../auth'
  import EditPage from './EditPage'
  import PageAccount from './PageAccount'
  import PageUpgrade from './PageUpgrade'
  import axios from 'axios'
  import SideNav from './SideNav';
  import store from '../store';

  export default {
    name: 'app',
    components: {
      SideNav, alertmessage, PageTable, PageTemplates, EditPage, PageAccount, PageUpgrade
    },
    data: () => ({
      drawer: true,
      userData: store.userData,
      portalData: store.portalData
    }),
    created() {
      const router = this.$router;
      auth.refreshAuth((user) => {
        if (!user) {
          router.history.push('/')
        }
      });
    },
    methods: {
      logout() {
        auth.logout()
        this.$router.history.push('/')
      }
    },
    computed: {
      currentUsername(){
        return this.userData.user ? this.userData.user.username : "loading...";
      }
    }
  }
</script>

