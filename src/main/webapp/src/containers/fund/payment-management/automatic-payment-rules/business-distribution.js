import React from 'react';
import config from 'config';
import ListSelector from 'widget/list-selector';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import { Form, Row, Col, Badge, Button, Checkbox, message, Input, Icon } from 'antd';
import { connect } from 'dva';
import AutomaticPaymentRulesService from './automatic-payment-rules.service';

const { Search } = Input;

class BusinessDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      businessTypeList: [
        { label: '规则描述', id: 'description' },
        { label: '付款公司', id: 'companyName' },
        { label: '付款账户', id: 'accountId' },
        { label: '启用', id: 'enabled' },
      ],
      businessTypeInfo: {},
      columns: [
        { title: '业务类型名称', dataIndex: 'busTypeCode' },
        { title: '业务类型代码', dataIndex: 'busTypeName' },
        // {
        //   title: '公司类型',
        //   dataIndex: 'companyTypeName',
        //   render: type => <span>{type ? type : '-'}</span>,
        // },
        {
          title: '启用',
          dataIndex: 'enabled',
          width: '8%',
          render: (enabled, record) => (
            <Checkbox defaultChecked={enabled} onChange={e => this.handleStatusChange(e, record)} />
          ),
        },
      ],
      data: [],
      dataTemp: [], // 临时存储data
      // defineId: this.props.match.params.id, // 当前类型定义的id ---用于保存，修改，获取baseInfo
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      showListSelector: false,
      selectorItem: {
        // 模态框表格
        title: '批量分配业务类型',
        // url: `${config.fundUrl}/api/payment/rule/company/${
        //   props.match.params.id
        // }/company/filter?setOfBooksId=${this.props.match.params.setOfBooksId}`,
        url: `${config.payUrl}/api/cash/flow/items/query?setOfBookId=1083762150064451585`,
        searchForm: [
          // 模态框头部输入框
          { type: 'input', id: 'businessCode', label: '业务代码' },
          { type: 'input', id: 'businessName', label: '业务名称' },
          { type: 'input', id: 'businessCodeFrom', label: '业务代码从' },
          { type: 'input', id: 'businessCodeTo', label: '业务代码至' },
        ],
        columns: [
          // table列
          { title: '现金流量项代码', dataIndex: 'flowCode' },
          { title: '现金流量项名称', dataIndex: 'description' },
          // {
          //   title: '状态',
          //   dataIndex: 'companyTypeName',
          //   render: value => (value || '-'),
          // },
        ],
        key: 'id',
      },
    };
  }

  componentWillMount() {
    this.getBasicInfo();
    this.getList();
    // const { match } = this.props;
    // this.setState({
    //   defineId: match.params.id,
    // });
  }

  /**
   * 打开分配业务的模态框
   */
  handleListShow = flag => {
    this.setState({ showListSelector: flag });
  };

  /**
   * 分配公司模态框表单的确认事件
   */
  handleListOk = values => {
    if (!values.result || !values.result.length) {
      this.handleListShow(false);
      return;
    }
    // eslint-disable-next-line no-param-reassign
    values = values.result.map(item => item.id); // 单选、多选，获取选中的公司ID
    const { match } = this.props;
    const params = [];
    values.forEach(item => {
      params.push({
        headId: match.params.id,
        companyId: item,
      });
    });
    AutomaticPaymentRulesService.batchDistributeBusiness(params)
      .then(() => {
        message.success('操作成功');
        this.handleListShow(false);
        this.getList();
      })
      .catch(e => {
        if (e.response) {
          message.error(`操作失败，${e.response.data.message}`);
        }
      });
  };

  /**
   * 根据头id获得该行详情------显示到各分配页面的头上
   */
  getBasicInfo = () => {
    const { match } = this.props;
    AutomaticPaymentRulesService.getInfoById(match.params.id).then(res => {
      this.setState({ businessTypeInfo: res.data });
    });
  };

  /**
   * 获取列表数据
   */
  getList = () => {
    const { match } = this.props;
    const { pagination, page } = this.state;
    this.setState({ loading: true });
    AutomaticPaymentRulesService.getDistributiveBusiness(
      pagination.page,
      pagination.pageSize,
      match.params.id
    ).then(res => {
      this.setState({
        data: res.data,
        loading: false,
        pagination: {
          total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
          current: page + 1,
          onChange: this.onChangePaper,
          onShowSizeChange: this.onShowSizeChange,
          showTotal: total => this.$t({ id: 'common.total' }, { total }),
        },
      });
    });
  };

  /**
   * 分配业务添加按钮
   */
  handAddBusiness = () => {
    const { data, dataTemp } = this.state;
    const value = {
      id: new Date().toJSON(),
      headerFlag: true,
      sequence: 10,
      status: 'new',
    };
    dataTemp[data.length] = value;
    data.push(value);
    this.setState({ data, dataTemp });
  };

  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = current => {
    this.setState(
      {
        page: current - 1,
        // pageSize,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 数据分页点击
   */
  onChangePaper = pagination => {
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
   *  选中启用状态框
   */
  handleStatusChange = (e, record) => {
    const params = {
      id: record.id,
      enabled: e.target.checked,
      versionNumber: record.versionNumber,
    };
    AutomaticPaymentRulesService.updateAssignBusiness(params)
      .then(() => {
        this.getList();
        message.success('操作成功');
      })
      .catch(() => {
        message.error(`${e.response.data.message}`);
      });
  };

  /**
   * 返回上一页
   */
  handleBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/payment-management/automatic-payment-rules/automatic-payment-rules/',
      })
    );
  };

  render() {
    const {
      loading,
      selectorItem,
      showListSelector,
      pagination,
      columns,
      data,
      businessTypeList,
      businessTypeInfo,
    } = this.state;
    const periodRow = [];
    const periodCol = [];
    // eslint-disable-next-line array-callback-return
    businessTypeList.map((item, index) => {
      if (index <= 2) {
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <div style={{ wordWrap: 'break-word' }}>
              {item.id === 'setOfBooksName'
                ? businessTypeInfo[item.id]
                  ? `${businessTypeInfo.setOfBooksCode} - ${businessTypeInfo.setOfBooksName}`
                  : '-'
                : businessTypeInfo[item.id]}
            </div>
          </Col>
        );
      }
      if (index === 3) {
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <Badge
              status={businessTypeInfo[item.id] ? 'success' : 'error'}
              text={businessTypeInfo[item.id] ? '启用' : '禁用'}
            />
          </Col>
        );
      }
      // return 'AAA';
    });
    periodRow.push(
      <Row
        style={{ background: '#f7f7f7', padding: '20px 25px 0', borderRadius: '6px 6px 0 0' }}
        key="1"
      >
        {periodCol}
      </Row>
    );
    return (
      <div className="company-distribution" style={{ paddingBottom: 20 }}>
        {periodRow}
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={8}>
                <Button type="primary" onClick={() => this.handleListShow(true)}>
                  分配业务类型
                </Button>
              </Col>
              {/* 模糊查询 */}
              <Col span={8} offset={8}>
                <Search
                  placeholder="请输入业务类型代码/名称"
                  enterButton
                  onSearch={this.searchByBusiness}
                />
              </Col>
            </Row>
          </div>
        </div>
        <Table
          rowKey={record => record.id}
          columns={columns}
          dataSource={data}
          loading={loading}
          onChange={this.onChangePaper}
          bordered
          pagination={pagination}
          size="middle"
        />
        <ListSelector
          visible={showListSelector}
          onCancel={() => this.handleListShow(false)}
          selectorItem={selectorItem}
          onOk={this.handleListOk}
        />
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />返回
        </a>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(BusinessDistribution);

export default connect()(wrappedCompanyDistribution);
