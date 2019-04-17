import React from 'react';
import config from 'config';
import { Form, Row, Col, Badge, Button, Checkbox, message, Icon } from 'antd';
import Table from 'widget/table';
import ListSelector from 'widget/list-selector';
import Service from './service';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';

class CompanyDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      companyTypeList: [
        { label: this.$t({ id: 'pre.payment.setOfBookName' }), id: 'setOfBooksName' },
        { label: this.$t('billing.type.code'), id: 'reportTypeCode' },
        { label: this.$t('billing.type.name'), id: 'reportTypeName' },
        { label: this.$t('common.column.status'), id: 'enabled' },
      ],
      companyTypeInfo: {},
      columns: [
        {
          title: this.$t('billing.expense.companyCode'),
          dataIndex: 'companyCode',
          align: 'center',
        },
        {
          title: this.$t('billing.expense.companyName'),
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: this.$t('billing.expense.companyType'),
          dataIndex: 'companyType',
          align: 'center',
          render: type => <span>{type ? type : '-'}</span>,
        },
        {
          title: this.$t('common.enabled'),
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
        title: this.$t('budget.item.batchCompany'),
        url: `${config.expenseUrl}/api/expense/report/type/company/filter`,
        searchForm: [
          { type: 'input', id: 'companyCode', label: this.$t('chooser.data.companyCode') },
          { type: 'input', id: 'companyName', label: this.$t('chooser.data.companyName') },
          { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
          { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
        ],
        columns: [
          { title: this.$t('chooser.data.companyCode'), dataIndex: 'companyCode' },
          { title: this.$t('chooser.data.companyName'), dataIndex: 'name' },
          {
            title: this.$t('billing.expense.companyType'),
            dataIndex: 'companyTypeName',
            render: value => (value ? value : '-'),
          },
        ],
        key: 'id',
      },
    };
  }
  componentWillMount() {
    this.getBillingInfo();
    this.getList();
  }

  //  得到上一页面的数据
  getBillingInfo = () => {
    const { match } = this.props;
    Service.getInfoById(match.params.id).then(res => {
      console.log(res, 'res');
      this.setState({ companyTypeInfo: res.data.expenseReportType });
    });
  };

  // 获得当页数据
  getList = () => {
    const { match } = this.props;
    const { page, pageSize } = this.state;
    this.setState({ loading: true });
    Service.getDistributiveCompany(match.params.id).then(res => {
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

  // 页面跳转
  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };

  // 是否启用
  handleStatusChange = (e, record) => {
    const { match } = this.props;
    let params = {
      id: record.id,
      companyId: record.companyId,
      companyCode: record.companyCode,
      companyType: record.companyType,
      companyName: record.companyName,
      reportTypeId: match.params.id,
      enabled: e.target.checked,
      versionNumber: record.versionNumber,
    };
    Service.updateAssignCompany(params)
      .then(res => {
        this.getList();
        message.success(this.$t('common.operate.success'));
      })
      .catch(e => {
        message.error(`${e.response.data.message}`);
      });
  };

  // 分配公司
  handleListShow = flag => {
    this.setState({ showListSelector: flag });
  };

  // 分配公司确定
  handleListOk = values => {
    if (!values.result || !values.result.length) {
      this.handleListShow(false);
      return;
    }
    let paramsValue = [];
    values = values.result.map(item => {
      paramsValue.push({
        reportTypeId: this.props.match.params.id,
        companyId: item.id,
        companyCode: item.companyCode,
        enabled: item.enabled,
      });
    });
    Service.batchDistributeCompany(paramsValue)
      .then(res => {
        message.success(this.$t('common.operate.success'));
        this.handleListShow(false);
        this.getList();
      })
      .catch(e => {
        if (e.response) {
          message.error(`${this.$t('common.operate.filed')}，${e.response.data.message}`);
        }
      });
  };

  // 返回到报账单类型首页
  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/document-type-manage/definition-billing-type/definition-billing-type',
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
              {this.$t('chooser.data.distribute.company')}
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
          extraParams={{ reportTypeId: this.props.match.params.id }}
          onOk={this.handleListOk}
        />
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t('pay.backlash.goback')}
        </a>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(CompanyDistribution);

export default connect()(wrappedCompanyDistribution);
