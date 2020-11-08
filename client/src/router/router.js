import Main from '@/view/main'

export const commonRouter = [
  {
    path: '/',
    title: '首页',
    hidden: true,
    component: Main,
    children: [
      {
        path: '/home',
        component: () => import('@/view/home')
      }
    ]
  }
]

export const appRouter = [
  {
    path: '/stock-normal',
    title: '基本信息',
    icon: 'el-icon-location',
    component: Main,
    children: [
      {
        path: '/stock-normal/list',
        title: '股票信息',
        component: () => import('@/view/stock/list')
      },
      {
        path: '/stock-normal/history',
        title: '股票历史',
        component: () => import('@/view/stock/history')
      },
      {
        path: '/stock-normal/indicator',
        title: '股票市盈率',
        component: () => import('@/view/stock/indicator')
      }
    ]
  },
  {
    path: '/stock-quantify',
    title: '量化操作',
    icon: 'el-icon-trophy',
    component: Main,
    children: [
      {
        path: '/stock-quantify/operate',
        title: '股票操作',
        component: () => import('@/view/stock/operate')
      },
      {
        path: '/stock-quantify/back-test',
        title: '策略回测',
        component: () => import('@/view/stock/back-test')
      },
      {
        path: '/stock-quantify/simulation',
        title: '模拟操作',
        component: () => import('@/view/stock/simulation')
      },
      {
        path: '/stock-quantify/search-best',
        title: '回测排行',
        component: () => import('@/view/stock/search-best')
      },
      {
        path: '/stock-quantify/search-best/detail',
        title: '回测明细',
        hidden: true,
        component: () => import('@/view/stock/search-best/detail')
      },
      {
        path: '/stock-quantify/search-params/detail',
        title: '寻找最优解',
        component: () => import('@/view/stock/search-params')
      },
      {
        path: '/stock-quantify/potential',
        title: '寻找潜在股票',
        component: () => import('@/view/stock/potential')
      }
    ]
  },
  {
    path: '/finance',
    title: '日常收支',
    icon: 'el-icon-location',
    component: Main,
    children: [
      {
        path: '/finance/income',
        title: '收支记录',
        component: () => import('@/view/finance/income')
      }
    ]
  }
]
