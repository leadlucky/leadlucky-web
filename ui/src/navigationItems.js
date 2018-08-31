import store from './store'

export default [
  {
    name: "Dashboard",
    icon: "dashboard",
    route: "/dashboard"
  },
  {
    name: "Create Page",
    icon: "pencil",
    route: "/dashboard/themes",
  },
  {
    name: "Settings",
    icon: "sliders",
    route: "/dashboard/account"
  },
  {
    name: "Upgrade",
    icon: "arrow-up",
    route: "/dashboard/upgrade",
    condition: () => store.userData.user.premiumStatus !== 'active'
  }
]
