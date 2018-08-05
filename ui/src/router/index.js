import Vue from 'vue'
import Router from 'vue-router'
import App from '../components/App'
import NewLogin from '../components/login/NewLogin'
import Coins from '../components/Coins'
import EditPage from '../components/EditPage'
import PageLegal from '../components/legal'
import PagePrivacy from '../components/privacy'
import themes from '@leadlucky/leadlucky-themes';
import PageTemplates from '../components/PageTemplates'
import PageAccount from '../components/PageAccount'
import PageUpgrade from '../components/PageUpgrade'
import PageTable from '../components/PageTable'
import Analytics from '../components/Analytics'

Vue.use(Router);


export default new Router({
  routes: [
    {
      path: '/dashboard',
      component: App,
      children: [
        {
          path: '/',
          name: 'pages-list',
          component: PageTable
        },
        {
          path: 'themes',
          name: 'themes-list',
          component: PageTemplates
        },
        {
          path: 'account',
          name: 'account-settings',
          component: PageAccount
        },
        {
          path: 'upgrade',
          name: 'upgrade',
          component: PageUpgrade
        },
        {
          path: 'edit/:themeName',
          name: 'edit-page',
          component: EditPage
        },
        {
          path: 'analytics/:pageName',
          name: 'analytics-page',
          component: Analytics
        }
      ]
    },
    {
      path: '/',
      name: 'NewLogin',
      component: NewLogin
    },
    {
      path: '/referral/:referrer',
      name: 'NewLoginRefer',
      component: NewLogin
    },
    {
      path: '/coins/:id',
      name: 'Coins',
      component: Coins
    },

    {
      path: '/legal',
      name: 'PageLegal',
      component: PageLegal
    },
    {
      path: '/privacy',
      name: 'privacyOG',
      component: PagePrivacy
    },
    ...Object.keys(themes).map(themeName => ({
        path: `/${themeName}/:id`,
        name: themeName,
        component: themes[themeName].component,
        props: {themeName}
    })),
    ...Object.keys(themes).map(themeName => {
      return {
        path: `/${themeName}`,
        name: `${themeName}-demo`,
        component: themes[themeName].component,
        props: {themeName}
      }
    })
  ]
})
