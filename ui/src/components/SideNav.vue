<template>
  <v-navigation-drawer
    clipped
    fixed
    v-model="visible"
    app
  >
    <v-list dense>
      <v-list-tile
        v-for="navItem in navigationItems"
        v-if="navItem.condition ? navItem.condition() : true"
        @click="$router.history.push(navItem.route)"
        :key="`nav-${navItem.name}`"
        active
      >
        <v-list-tile-action>
          <v-icon>{{navItem.icon}}</v-icon>
        </v-list-tile-action>
        <v-list-tile-content>
          <v-list-tile-title>{{navItem.name}}</v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>
    </v-list>
    <v-divider></v-divider>
  </v-navigation-drawer>
</template>

<script>
    import navigationItems from '../navigationItems'

    export default {
        name: "side-nav",
        props: {
          visible: {
            type: Boolean,
            default: true
          }
        },
        computed: {
          navigationItems: () => navigationItems
        }
    }
</script>

<style scoped>

</style>
