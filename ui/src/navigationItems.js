import store from './store'

export default [
  {
    name: "Dashboard",
    icon: "dashboard",
    route: "/dashboard"
  },
  {
    name: "Create Page",
    icon: "create",
    route: "/dashboard/themes",
  },
  {
    name: "Settings",
    icon: "settings",
    route: "/dashboard/account"
  },
  {
    name: "Upgrade",
    icon: "payment",
    route: "/dashboard/upgrade",
    condition: () => store.userData.user.premiumStatus !== 'active'
  }
]
