import React, { Component } from 'react';
import CustomTable from 'widget/custom-table';
import config from 'config';
import { connect } from 'dva';
// import { routerRedux } from 'dva/router';
import { Popover, Modal, Badge } from 'antd';
import BudgetDetail from '../budget-details';

class ChildrenItem extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: this.$t('contract.project.req.num'),
          dataIndex: 'projectReqNumber',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('contract.project.name'),
          dataIndex: 'projectName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('common.applicant'),
          dataIndex: 'employeeName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('common.apply.data'),
          dataIndex: 'applyDate',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('contract.project.leader'),
          dataIndex: 'pmName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('contract.start.date'), // '启用日期',
          dataIndex: 'startUseDate',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('contract.close.date'), // '禁用日期',
          dataIndex: 'closeUseDate',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('contract.project.mark'), // '立项标志',
          dataIndex: 'projectFlag',
          align: 'center',
          width: 120,
          render: text => {
            return (
              <Popover
                content={text ? this.$t('contract.hasProject') : this.$t('contract.noProject')} // '已立项' : '未立项'
              >
                {text ? this.$t('contract.hasProject') : this.$t('contract.noProject')}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.approval.status'), // '审批状态',
          dataIndex: 'status',
          align: 'center',
          width: 120,
          render: status => {
            return (
              <Popover content={this.$statusList[status].label}>
                <Badge
                  status={this.$statusList[status].state}
                  text={this.$statusList[status].label}
                />
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.project.budget'), // '项目预算',
          dataIndex: 'operation',
          align: 'center',
          width: 150,
          fixed: 'right',
          render: (text, record) => {
            return (
              <a
                onClick={e => {
                  this.handleBudgetView(e, record);
                }}
              >
                {/* 预算 */}
                {this.$t('contract.budget')}
              </a>
            );
          },
        },
      ], // 表格列
      budgetDetailShow: false,
    };
  }

  // 预算
  handleBudgetView = (e, record) => {
    e.preventDefault();
    // const { dispatch } = this.props;
    // dispatch(
    //   routerRedux.replace({
    //     pathname: `/project-manage/my-project-apply/budget-details/${record.id}`,
    //   })
    // );
    this.setState({
      budgetDetailId: record.id,
      budgetDetailShow: true,
    });
  };

  render() {
    const { columns, budgetDetailId, budgetDetailShow } = this.state;
    const { id } = this.props;
    return (
      <div>
        <CustomTable
          columns={columns}
          url={`${
            config.contractUrl
          }/api/project/requisition/query/by/parent/project/requisition/id/${id}`}
          scroll={{ x: 1300 }}
        />
        <Modal
          title="预算日记账"
          visible={budgetDetailShow}
          onCancel={() => {
            this.setState({ budgetDetailShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <BudgetDetail id={budgetDetailId || ''} readOnly />
        </Modal>
      </div>
    );
  }
}

export default connect()(ChildrenItem);
