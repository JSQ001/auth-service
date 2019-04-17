import React from 'react';
import { Table, Row, Col, Button, Badge, Form } from 'antd';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { messages } from 'utils/utils';
// import moment from 'moment';
import SlideFrame from 'widget/slide-frame';
import FundSearchForm from '../../fund-components/fund-search-form';
import AccountTransferSettingService from './account-transfer-setting.service';
import AccountTransferSlide from './account-transfer-slide';

class AccountTransferRelationshipSetting extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      priority: '',
      // isNew: true,
      slideVisible: false, // 侧边栏显示
      editModel: {}, // 点击行时的编辑数据
      loading: false, // loading状态
      searchParams: {}, // 模糊查询数据搜索参数
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '资金池母账号',
          id: 'capitalPoolMasterAccount',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '资金池母账户',
          id: 'poolMotherAccount',
        },
        {
          colSpan: 6,
          type: 'enabledStateSelect',
          label: '状态',
          id: 'state',
          options: [{ value: 0, name: '禁用' }, { value: 1, name: '启用' }],
          valueListCode: 'ZJ_STATUS',
        },
        // {
        //   colSpan: 6,
        //   type: 'input',
        //   label: '创建人',
        //   id: 'creater',
        // },
        {
          colSpan: 6,
          type: 'modalList',
          label: '创建人',
          id: 'creater',
          listType: 'select_authorization_user',
          labelKey: 'userName',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'userId',
          single: true,
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '所属公司',
          id: 'belongCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        // {
        //   colSpan: 6,
        //   type: 'modalList',
        //   label: '所属银行',
        //   id: 'belongBank',
        //   listType: 'paymentAccount',
        // },
        {
          colSpan: 6,
          type: 'valueList',
          label: '所属银行',
          id: 'belongBank',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
      ],
      columns: [
        {
          title: '序号',
          dataIndex: 'priority',
          width: 50,
          // render: (value, record, index) => (
          //   <span
          //     onClick={event => {
          //       event.stopPropagation();
          //       console.log('序号。。', record, index);
          //       this.setState({
          //         priority: index + 1,
          //       });
          //     }}
          //   >
          //     {index + 1}
          //   </span>
          // ),
        },
        {
          title: '资金池母账号',
          dataIndex: 'bankAccount',
          width: 100,
        },
        {
          title: '资金池母账户',
          dataIndex: 'bankAccountName',
          width: 100,
        },
        {
          title: '所属银行',
          dataIndex: 'bankName',
          width: 100,
        },
        {
          title: '所属公司',
          dataIndex: 'companyName',
          width: 100,
        },
        {
          title: '子账户分配',
          width: 60,
          render: record => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.distribution(record.id);
              }}
            >
              {' '}
              分配
            </a>
          ),
        },
        {
          title: '创建人',
          dataIndex: 'createdName',
          width: 100,
        },
        {
          title: '创建时间',
          dataIndex: 'createdDateDesc',
          width: 100,
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          width: 50,
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={enabled ? messages('common.status.enable') : messages('common.status.disable')}
            />
          ),
        },
        {
          title: '操作',
          dataIndex: '',
          width: 50,
          render: () => <a> 编辑</a>,
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 列表数据
   */
  getList() {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    AccountTransferSettingService.getAccountTransferList(
      pagination.page,
      pagination.pageSize,
      searchParams
    ).then(response => {
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
    });
    // .catch(err => {
    //   message.error(err);
    // });
  }

  /**
   * 更多展示中的搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          accountCode: values.capitalPoolMasterAccount, // 资金池母账号（input输入框）
          accountName: values.poolMotherAccount, // 资金池母账户（input输入框）
          createBy: values.creater && values.creater[0] ? values.creater[0].userId : '', // 创建人
          // enabled: values.state ? (values.state.key === 'true' ? '1' : '0') : '', // 状态
          enabled: values.state ? values.state.key : '',
          companyId:
            values.belongCompany && values.belongCompany[0] ? values.belongCompany[0].id : '', // 所属公司（弹框）
          bankCode: values.belongBank ? values.belongBank.key : '', // 所属银行(select框---值列表)
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 跳转分配页面
   */
  distribution = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/transfer-management/account-transfer-relationship-setting/account-transfer-relationship-setting-distribution/${id}`,
      })
    );
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

  /**
   * 改变每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const temp = {};
    temp.page = current - 1;
    temp.pageSize = pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 新建
   */
  handleCreateClick = () => {
    AccountTransferSettingService.numberIncrease().then(response => {
      console.log('response', response);
      const numOrder = response.data;
      this.setState({ slideVisible: true, priority: numOrder });
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

  render() {
    const {
      priority,
      loading,
      editModel,
      slideVisible,
      columns,
      searchForm,
      pagination,
      tableData,
    } = this.state;
    return (
      <div className="account-transfer-setting">
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <div>
          <div style={{ margin: '10px 0' }}>
            <Row>
              <Col span={10}>
                <Button
                  onClick={this.handleCreateClick}
                  style={{ marginRight: '10px' }}
                  type="primary"
                >
                  {this.$t('common.create')}
                </Button>
              </Col>
            </Row>
            {/* 侧边栏 */}
            <SlideFrame
              title={editModel.id ? '编辑' : '新建'}
              show={slideVisible}
              onClose={() => this.handleClose()}
            >
              <AccountTransferSlide
                onClose={this.handleClose}
                rowpParams={editModel}
                priority={priority}
              />
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
            bordered
            size="middle"
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePager}
            scroll={{ x: '130%' }}
          />
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(Form.create()(AccountTransferRelationshipSetting));

// const wrappedCompanyDistribution = Form.create()(AccountTransferRelationshipSetting);

// export default connect()(wrappedCompanyDistribution);
