<template>
  <div class="left-sidebar">
    <!-- Sidebar scroll-->
    <div class="slimScrollDiv" style="position: relative; overflow: visible; width: auto; height: 100%;">
      <div class="scroll-sidebar" style="overflow: hidden; width: auto; height: 100%;">
        <!-- Sidebar navigation-->
        <nav class="sidebar-nav active">
          <ul id="sidebarnav" class="in">
            <li v-for="navItem in navigationItems" :class=" activePath === navItem.route && 'active'">
              <router-link :to="navItem.route">
                <i :class="`fa fa-${navItem.icon}`"></i>
                <span class="hide-menu">{{navItem.name}}</span>
              </router-link>
            </li>
          </ul>
        </nav>
        <!-- End Sidebar navigation -->
      </div>
      <div class="slimScrollBar"
           style="background: rgb(220, 220, 220); width: 5px; position: absolute; top: 0px; opacity: 0.4; display: none; border-radius: 7px; z-index: 99; left: 1px; height: 1236px;"></div>
      <div class="slimScrollRail"
           style="width: 5px; height: 100%; position: absolute; top: 0px; display: none; border-radius: 7px; background: rgb(51, 51, 51); opacity: 0.2; z-index: 90; left: 1px;"></div>
    </div>
    <!-- End Sidebar scroll-->
  </div>
</template>

<script>
  import navigationItems from '../navigationItems'

  export default {
    name: "side-nav",
    props: {
      mini: {
        type: Boolean,
        default: false
      }
    },
    computed: {
      navigationItems: () => navigationItems
    },
    data(){
      return {activePath: this.$router.currentRoute.path}
    },
    watch: {
      $route (to, _) {
        this.activePath = to.path
      }
    }
  }
</script>

<style scoped>

</style>
