import React from 'react';

import config from 'config';
import { Form, Row, Col, Badge, Button, Checkbox, message, Icon } from 'antd';
import Table from 'widget/table';
import ListSelector from 'widget/list-selector';
import service from './service';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';

class CompanyDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      companyTypeList: [
        { label: '账套', id: 'setOfBooksName' },
        { label: '差旅申请单类型代码', id: 'code' },
        { label: '差旅申请单类型名称', id: 'name' },
        { label: '状态', id: 'enabled' },
      ],
      companyTypeInfo: {},
      columns: [
        { title: '公司代码', dataIndex: 'companyCode', align: 'center' },
        { title: '公司名称', dataIndex: 'companyName', align: 'center' },
        {
          title: '公司类型',
          dataIndex: 'companyType',
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
        showQuickJumper: true,
        showSizeChanger: true,
      },
      page: 0,
      pageSize: 10,
      showListSelector: false,
      selectorItem: {
        title: '批量分配公司',
        url: `${config.expenseUrl}/api/travel/application/type/company/${
          props.match.params.id
        }/query/filter?setOfBooksId=${this.props.match.params.setOfBooksId}`,
        searchForm: [
          { type: 'input', id: 'companyCode', label: '公司代码' },
          { type: 'input', id: 'companyName', label: '公司名称' },
          { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
          { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
        ],
        columns: [
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
  getBasicInfo = () => {
    const { match } = this.props;
    service.getInfoById(match.params.id).then(res => {
      this.setState({ companyTypeInfo: res.data });
    });
  };
  getList = () => {
    const { match } = this.props;
    const { page, pageSize } = this.state;
    this.setState({ loading: true });
    service.getDistributiveCompany(match.params.id).then(res => {
      this.setState({
        data: res.data,
        loading: false,
        pagination: {
          total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
          current: page + 1,
          onChange: this.onChangePaper,
          onShowSizeChange: this.onShowSizeChange,
          showTotal: total => this.$t({ id: 'common.total' }, { total: total }),
        },
      });
    });
  };
  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        page: current - 1,
        pageSize,
      },
      () => {
        this.getList();
      }
    );
  };
  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };

  handleStatusChange = (e, record) => {
    let params = {
      id: record.id,
      enabled: e.target.checked,
      versionNumber: record.versionNumber,
    };
    service
      .updateAssignCompany(params)
      .then(res => {
        this.getList();
        message.success('操作成功');
      })
      .catch(e => {
        message.error(`${e.response.data.message}`);
      });
  };

  handleListShow = flag => {
    this.setState({ showListSelector: flag });
  };

  handleListOk = values => {
    if (!values.result || !values.result.length) {
      this.handleListShow(false);
      return;
    }

    let paramsValue = [];
    paramsValue.sobPayReqTypeId = this.props.match.params.id;
    paramsValue.companyId = [];
    paramsValue.compcompanyCodeanyId = [];

    values = values.result.map(item => item.id);

    service
      .batchDistributeCompany(this.props.match.params.id, values)
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
  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          '/document-type-manage/travel-application-type/travel-application-type/' +
          this.state.companyTypeInfo.setOfBooksId,
      })
    );
  };
  render() {
    const {
      loading,
      companyTypeList,
      companyTypeInfo,
      pagination,
      columns,
      data,
      showListSelector,
      selectorItem,
    } = this.state;
    let periodRow = [];
    let periodCol = [];
    companyTypeList.map((item, index) => {
      index <= 2 &&
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <div style={{ wordWrap: 'break-word' }}>
              {item.id === 'setOfBooksName'
                ? companyTypeInfo[item.id]
                  ? companyTypeInfo.setOfBooksCode + ' - ' + companyTypeInfo.setOfBooksName
                  : '-'
                : companyTypeInfo[item.id]}
            </div>
          </Col>
        );
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
            <Button type="primary" onClick={() => this.handleListShow(true)}>
              分配公司
            </Button>
          </div>
        </div>
        <Table
          rowKey={record => record.companyId}
          columns={columns}
          dataSource={data}
          loading={loading}
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