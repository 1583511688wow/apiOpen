export default [
  {path: '/', name: '主页', icon: 'smile', component: './Index'},

  {path: '/interface_info/:id', name: '查看接口', icon: 'smile', component: './InterfaceInfo', hideInMenu: true},
  {
    path: '/user',
    layout: false,
    routes: [{name: '登录', path: '/user/login', component: './User/Login'},
      {name: '注册', path: '/user/register', component: './User/Register'},
    ],
  },


  {path: '/user/management', name: '个人用户管理', icon: 'table', component: './User/Management'},


  {path: '/user/buyinterface', name: '接口大全', icon: 'table', component: './User/Buyinterface'},


  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {name: '接口管理', icon: 'table', path: '/admin/interface_info', component: './Admin/InterfaceInfo'},
      {name: '接口分析', icon: 'analysis', path: '/admin/interface_analysis', component: './Admin/InterfaceAnalysis'},
    ],
  },

  // { path: '/', redirect: '/welcome' },
  {path: '*', layout: false, component: './404'},
];
