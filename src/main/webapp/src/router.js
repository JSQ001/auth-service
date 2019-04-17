import React from 'react';
import { routerRedux, Route, Switch } from 'dva/router';
import { Spin } from 'antd';
import dynamic from 'dva/dynamic';
import { getRouterData } from './common/router';
import styles from './index.less';

import Container from './container';

const { ConnectedRouter } = routerRedux;

dynamic.setDefaultLoadingComponent(() => {
  return <Spin size="large" className={styles.globalSpin} />;
});

function RouterConfig({ history, app }) {
  const routerData = getRouterData(app);
  const UserLayout = routerData['/user'].component;
  return (
    <ConnectedRouter history={history}>
      <Switch>
        <Route path="/user" component={UserLayout} />
        <Route path="/" render={props => <Container routerData={routerData} {...props} />} />
      </Switch>
    </ConnectedRouter>
  );
}

export default RouterConfig;
