import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Tabs } from 'antd';
import ApprovalToPend from './approval-to-pend';
import RejectReport from './reject-report';
import UnFinishedReport from './unfinished-report';

const { TabPane } = Tabs;

class ToDoList extends Component {
  // tab选项卡切换
  tabChange = tabKey => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/approval-management/todo-list/todo-list/${tabKey}/0`,
      })
    );
  };

  render() {
    const {
      match: {
        params: { documentType, tab },
      },
    } = this.props;
    return (
      <Tabs defaultActiveKey={tab} activeKey={tab} onChange={this.tabChange}>
        <TabPane tab="待审批单据" key="to-do">
          <ApprovalToPend key={documentType} documentType={documentType} />
        </TabPane>
        <TabPane tab="被退回单据" key="reject">
          <RejectReport />
        </TabPane>
        <TabPane tab="未完成单据" key="un-finish">
          <UnFinishedReport />
        </TabPane>
      </Tabs>
    );
  }
}

export default connect()(ToDoList);
