import React from 'react';
import config from 'config';
import { Form, Row, Col, Badge, Button, Checkbox, Input, message, Icon } from 'antd';
import Table from 'widget/table';
import AutomaticPaymentRulesService from './automatic-payment-rules.service';
import { routerRedux } from 'dva/router';
import ListSelector from 'widget/list-selector';
import { connect } from 'dva';

const { Search } = Input;
// const { confirm } = Modal;

class CompanyDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      companyTypeList: [
        { label: '公司代码', id: 'companyCode' },
        { label: '公司名称', id: 'companyName' },
        { label: '公司类型', id: 'companyType' },
        { label: '状态', id: 'enabled' },
      ],
      companyTypeInfo: {}, // 存放一整行消息
      // 表格
      columns: [
        { title: '公司代码', dataIndex: 'companyCode', align: 'center' },
        { title: '公司名称', dataIndex: 'companyName', align: 'center' },
        {
          title: '公司类型',
          dataIndex: 'companyTypeName',
          align: 'center',
          render: type => <span>{type ? type : '-'}</span>,
        },
        {
          title: '启用',
          dataIndex: 'enabled',
          width: '8%',
          align: 'center',
          render: (enabled, record) => (
            <Checkbox defaultChecked={enabled} onChange={e => this.handleStatusChange(e, record)} />
          ),
        },
      ],
      data: [],
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
        title: '批量分配公司',
        url: `${config.fundUrl}/api/payment/rule/company/${
          props.match.params.id
        }/company/filter?setOfBooksId=${this.props.match.params.setOfBooksId}`,
        searchForm: [
          // 模态框头部输入框
          { type: 'input', id: 'companyCode', label: '公司代码' },
          { type: 'input', id: 'companyName', label: '公司名称' },
          { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
          { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
        ],
        columns: [
          // table列
          { title: '公司代码', dataIndex: 'companyCode' },
          { title: '公司名称', dataIndex: 'name' },
          {
            title: '公司类型',
            dataIndex: 'companyTypeName',
            render: value => (value ? value : '-'),
          },
        ],
        key: 'id',
      },
    };
  }

  componentWillMount() {
    this.getBasicInfo();
    this.getList();
  }

  /**
   * 获取基本信息
   * 根据id获取一整行的消息
   */
  getBasicInfo = () => {
    const { match } = this.props;
    AutomaticPaymentRulesService.getInfoById(match.params.id).then(res => {
      this.setState({ companyTypeInfo: res.data });
      // console.log('==companyTypeInfo==', this.state.companyTypeInfo)
    });
  };

  /**
   * 获取列表
   */
  getList = () => {
    const { match } = this.props;
    const { pagination } = this.state;
    this.setState({ loading: true });
    AutomaticPaymentRulesService.getDistributiveCompany(
      pagination.page,
      pagination.pageSize,
      match.params.id
    ).then(res => {
      this.setState({
        data: res.data,
        loading: false,
        pagination: {
          ...pagination,
          total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
          onChange: this.onChangePager,
          current: pagination.page + 1,
        },
      });
    });
  };

  /**
   * 数据分页点击
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
   * 启用状态
   */
  handleStatusChange = (e, record) => {
    let params = {
      id: record.id,
      enabled: e.target.checked,
      versionNumber: record.versionNumber,
    };
    AutomaticPaymentRulesService.updateAssignCompany(params)
      .then(res => {
        this.getList();
        message.success('操作成功');
      })
      .catch(e => {
        message.error(`${e.response.data.message}`);
      });
  };

  /**
   * 打开分配公司的模态框
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
    values = values.result.map(item => item.id); // 单选、多选，获取选中的公司ID
    let params = [];
    values.forEach(item => {
      params.push({
        headId: this.props.match.params.id,
        companyId: item,
      });
    });
    AutomaticPaymentRulesService.batchDistributeCompany(params)
      .then(res => {
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
   * 返回上一页
   */
  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          '/fund-setting/direct-link-parameter-definition/direct-link-parameter-definition/',
      })
    );
  };

  /**
   * 根据单据号搜索
   */
  searchByCompany = value => {
    let copyValue = {
      // documentNumber: value,
    };
    this.search(copyValue);
  };

  render() {
    const {
      searchForm,
      showListSelector,
      loading,
      columns,
      pagination,
      companyTypeInfo,
      companyTypeList,
      data,
      selectorItem,
    } = this.state;
    let periodCol = [];
    let periodRow = [];
    companyTypeList.map((item, index) => {
      index <= 2 &&
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <div style={{ wordWrap: 'break-word' }}>
              {item.id === 'setOfBooksName'
                ? companyTypeInfo[item.id]
                : // ? companyTypeInfo.setOfBooksCode + ' - ' + companyTypeInfo.setOfBooksName
                  // : '-'
                  companyTypeInfo[item.id]}
            </div>
          </Col>
        );
      // 启用状态
      index == 3 &&
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <Badge
              status={companyTypeInfo[item.id] ? 'success' : 'error'}
              text={companyTypeInfo[item.id] ? '启用' : '禁用'}
            />
          </Col>
        );
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
                  分配公司
                </Button>
              </Col>
              <Col span={8} offset={8}>
                <Search
                  placeholder="请输入公司代码/公司名称"
                  enterButton
                  onSearch={this.searchByCompany}
                />
              </Col>
            </Row>
          </div>
        </div>
        <Table
          rowKey={record => record.companyId}
          columns={columns}
          dataSource={data}
          loading={loading}
          onChange={this.onChangePager}
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

const wrappedCompanyDistribution = Form.create()(CompanyDistribution);

export default connect()(wrappedCompanyDistribution);
