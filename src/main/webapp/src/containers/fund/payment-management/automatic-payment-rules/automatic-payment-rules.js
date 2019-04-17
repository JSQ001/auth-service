import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Form, Badge, message } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import { messages } from 'utils/utils';
import AutomaticPaymentRulesService from './automatic-payment-rules.service';
import AutoPayRuleAdd from './auto-pay-rule-add';
import 'styles/fund/account.scss'; // 表格长度可以伸缩

class AutomaticPaymentRules extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      slideVisible: false, // 侧边栏显示
      loading: false, // 加载状态
      editModel: {}, // 点击行时的编辑数据
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      // table列表
      columns: [
        {
          title: '优先级',
          dataIndex: 'priority',
          width: 100,
          align: 'center',
        },
        {
          title: '描述规则',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '付款公司',
          dataIndex: 'companyName',
          width: 130,
          align: 'center',
        },
        {
          title: '付款账户',
          dataIndex: 'accountId',
          width: 130,
          align: 'center',
        },
        {
          title: '账户名称',
          dataIndex: 'accountName',
          width: 130,
          align: 'center',
        },
        {
          title: '分配公司',
          dataIndex: '',
          width: 100,
          align: 'center',
          render: record => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.distributionCompany(record);
              }}
            >
              {' '}
              分配
            </a>
          ),
        },
        {
          title: '业务类型',
          dataIndex: '',
          width: 100,
          align: 'center',
          render: id => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.distributionBusiness(id);
              }}
            >
              {' '}
              分配
            </a>
          ),
        },
        {
          title: '员工级别',
          dataIndex: '',
          width: 100,
          align: 'center',
          render: id => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.distributionEmploy(id);
              }}
            >
              {' '}
              分配
            </a>
          ),
        },
        {
          title: '系统来源',
          dataIndex: 'systemSourceDesc',
          width: 130,
          align: 'center',
        },
        {
          title: '对公对私',
          dataIndex: 'propFlagDesc',
          width: 130,
          align: 'center',
        },
        {
          title: '信用分区间',
          dataIndex: 'creditScore',
          width: 130,
          align: 'center',
        },
        {
          title: '金额区间',
          dataIndex: 'amount',
          width: 130,
          align: 'center',
        },
        {
          title: '启用',
          dataIndex: 'enabled',
          width: 100,
          align: 'center',
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={enabled ? messages('common.status.enable') : messages('common.status.disable')}
            />
          ),
        },
        {
          title: '编辑人',
          dataIndex: 'lastUpdatedByName',
          width: 100,
          // fixed: 'right',
          align: 'center',
        },
      ],
    };
  }

  componentWillMount() {
    this.getList();
  }

  /**
   * 列表数据
   */
  getList() {
    const { pagination } = this.state;
    this.setState({ loading: true });
    AutomaticPaymentRulesService.getAutoPayRuleHeadList(pagination.page, pagination.pageSize)
      .then(response => {
        const { data } = response;
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      })
      .catch(err => {
        message.error(err);
      });
  }

  /**
   * 跳到分配公司页面
   */
  distributionCompany = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/automatic-payment-rules/company-distribution/${record.id}`,
      })
    );
  };

  /**
   * 跳到分配员工页面
   */
  distributionEmploy = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/automatic-payment-rules/employ-distribution/${record.id}`,
      })
    );
  };

  /**
   * 跳到分配业务页面
   */
  distributionBusiness = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/automatic-payment-rules/business-distribution/${record.id}`,
      })
    );
  };

  /**
   * 新建按钮
   */
  handleCreateClick = () => {
    this.setState({
      slideVisible: true,
    });
  };

  /**
   * 关闭侧边栏
   */
  handleClose = value => {
    // console.log(value);
    this.setState({
      slideVisible: false,
      editModel: {},
    });
    if (value === 'save') {
      this.getList();
    }
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  render() {
    const {
      slideVisible,
      columns,
      loading,
      pagination,
      tableData, // 数据列表
      editModel,
    } = this.state;
    return (
      <div className="train">
        <div className="table-header-buttons" style={{ marginTop: '20px', marginBottom: '15px' }}>
          {/* 新建 */}
          <Button style={{ marginRight: '10px' }} type="primary" onClick={this.handleCreateClick}>
            {this.$t('common.create')}
          </Button>
          {/* 侧边栏 */}
          <SlideFrame
            title={editModel.id ? '自动付款规则编辑' : '新建'}
            show={slideVisible}
            onClose={() => this.handleClose()}
          >
            <AutoPayRuleAdd onClose={this.handleClose} params={editModel} />
          </SlideFrame>
        </div>

        <Table
          onRow={record => {
            return {
              onClick: () => {
                this.setState({ editModel: record, slideVisible: true });
              },
            };
          }}
          rowKey={record => record.id}
          columns={columns}
          dataSource={tableData}
          pagination={pagination}
          loading={loading}
          onChange={this.onChangePager}
          bordered
          style={{ marginTop: 5, cursor: 'pointer' }}
          size="middle"
          scroll={{ x: '130%' }}
        />
      </div>
    );
  }
}

function show(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(show)(Form.create()(AutomaticPaymentRules));
