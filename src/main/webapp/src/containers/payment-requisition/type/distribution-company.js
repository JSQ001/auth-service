import React from 'react';
import config from 'config';
import { Form, Row, Col, Badge, Button, Checkbox, message, Icon, Input, Affix } from 'antd';
import { routerRedux } from 'dva/router';
import ListSelector from 'widget/list-selector';
import httpFetch from 'share/httpFetch';
import CustomTable from 'widget/custom-table';
import { connect } from 'dva';

class AcpRequestTypesCompanyDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      companyTypeList: [
        { label: this.$t('payment.zhang.set'), id: 'setOfBooksCode' } /*账套*/,
        {
          label: this.$t('payment.payment.requisition.type.code'),
          id: 'acpReqTypeCode',
        } /*付款申请单类型代码*/,
        {
          label: this.$t('payment.payment.requisition.type.name'),
          id: 'description',
        } /*付款申请单类型名称*/,
        { label: this.$t('payment.type.status'), id: 'enabled' } /*类型状态*/,
      ],
      companyTypeInfo: {},
      columns: [
        { title: this.$t('payment.company.code'), dataIndex: 'companyCode' } /*公司代码*/,
        {
          title: this.$t('payment.the.name.of.the.company'),
          dataIndex: 'companyName',
        } /*公司名称*/,
        {
          title: this.$t('payment.the.company.type') /*公司类型*/,
          dataIndex: 'companyTypeName',
          render: type => <span>{type ? type : '-'}</span>,
        },
        {
          title: this.$t('payment.to.enable.the') /*启用*/,
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
        onChange: this.onChangePaper,
        onShowSizeChange: this.onChangePageSize,
      },
      page: 0,
      pageSize: 8,
      showListSelector: false,
      selectorItem: {},
      acpRequisitionTypeDefine:
        '/document-type-manage/payment-requisition-type/payment-requisition-type',
    };
  }

  componentWillMount() {
    this.getBasicInfo();
  }

  getBasicInfo = () => {
    const { params } = this.props.match;
    httpFetch.get(`${config.payUrl}/api/acp/request/type/queryById/${params.id}`).then(res => {
      let selectorItem = {
        title: this.$t('payment.mass.distribution.company') /*批量分配公司*/,
        url: `${config.payUrl}/api/acp/request/type/company/${
          params.setOfBooksId
        }/companies/query/filter`,
        searchForm: [
          // { type: 'input', id: 'setOfBooksCode', label: '账套', defaultValue: res.data.setOfBooksCode + '-' + res.data.setOfBooksName, disabled: true },
          { type: 'input', id: 'companyCode', label: this.$t('payment.company.code') } /*公司代码*/,
          {
            type: 'input',
            id: 'companyName',
            label: this.$t('payment.the.name.of.the.company'),
          } /*公司名称*/,
          {
            type: 'input',
            id: 'companyCodeFrom',
            label: this.$t('payment.the.company.code.from'),
          } /*公司代码从*/,
          {
            type: 'input',
            id: 'companyCodeTo',
            label: this.$t('payment.the.company.code.to'),
          } /*公司代码至*/,
        ],
        columns: [
          { title: this.$t('payment.company.code'), dataIndex: 'code' } /*公司代码*/,
          { title: this.$t('payment.the.name.of.the.company'), dataIndex: 'name' } /*公司名称*/,
          {
            title: this.$t('payment.the.company.type'),
            dataIndex: 'attribute4',
            render: value => value || '-',
          } /*公司类型*/,
        ],
        key: 'id',
      };
      this.setState({ companyTypeInfo: res.data, selectorItem });
    });
  };

  handleStatusChange = (e, record) => {
    let params = {
      id: record.id,
      enabled: e.target.checked,
      versionNumber: record.versionNumber,
    };
    httpFetch
      .put(
        `${config.payUrl}/api/acp/request/type/company/${
          this.props.match.params.setOfBooksId
        }/updateCompany`,
        params
      )
      .then(res => {
        if (res.status === 200) {
          this.refs.table.search();
          message.success(this.$t('payment.operation.is.successful')); /*操作成功*/
        }
      })
      .catch(e => {
        message.error(`操作失败，${e.response.data.message}`);
      });
  };

  handleListShow = flag => {
    this.setState({ showListSelector: flag });
  };

  handleListOk = values => {
    if (values && values.result.length) {
      const { params } = this.props.match;
      let paramsValue = {};
      paramsValue.acpReqTypesId = params.id;
      paramsValue.companyIds = [];
      values.result.map(item => {
        paramsValue.companyIds.push(item.id);
      });
      httpFetch
        .post(
          `${config.payUrl}/api/acp/request/type/company/${params.setOfBooksId}/batchAssignCompany`,
          paramsValue
        )
        .then(res => {
          if (res.status === 200) {
            message.success(this.$t('payment.operation.is.successful')); /*操作成功*/
            this.handleListShow(false);
            this.refs.table.search();
          }
        })
        .catch(e => {
          if (e.response) {
            message.error(`操作失败，${e.response.data.message}`);
          }
        });
    } else {
      message.warn(this.$t('payment.please.select.a.company')); /*请选择公司*/
    }
  };

  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: this.state.acpRequisitionTypeDefine,
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
          <Col span={6} key={item.id}>
            <div>{item.label}</div>
            <div>
              {item.id === 'setOfBooksCode'
                ? companyTypeInfo[item.id]
                  ? companyTypeInfo[item.id] + ' - ' + companyTypeInfo.setOfBooksName
                  : '-'
                : companyTypeInfo[item.id]}
            </div>
            {/* <Input disabled value={item.id==="setOfBooksCode"?companyTypeInfo[item.id] ? companyTypeInfo[item.id] + ' - ' + companyTypeInfo.setOfBooksName : '-' :
                companyTypeInfo[item.id]}/> */}
          </Col>
        );
      if (index === 2) {
        periodRow.push(...periodCol);
      }
      if (index === 3) {
        periodRow.push(
          <Col span={6} key={index}>
            <div>{this.$t('paymentmethod.isenabled')}</div>
            {/*状态*/}
            <div>
              <Badge
                status={companyTypeInfo[item.id] ? 'success' : 'error'}
                text={
                  companyTypeInfo[item.id]
                    ? this.$t('payment.to.enable.the')
                    : this.$t('payment.disable')
                } /*禁用*/ /*启用*/
                style={{ textAlign: 'center' }}
              />
            </div>
          </Col>
        );
      }
    });

    return (
      <div className="company-distribution" style={{ paddingBottom: '20px' }}>
        <Row
          gutter={24}
          style={{
            margin: '0px 5px 15px',
            backgroundColor: '#FAFAFA',
            padding: '20px 25px 20px 10px',
          }}
        >
          {periodRow}
        </Row>

        {/* <div className='divider'></div> */}
        <div className="table-header">
          {/* <div className="table-header-title">{`共搜索到 ${pagination.total} 条数据`}</div> */}
          <div className="table-header-buttons">
            <Button type="primary" onClick={() => this.handleListShow(true)}>
              {this.$t('payment.mass.distribution.company')}
              {/*批量分配公司*/}
            </Button>
          </div>
        </div>
        <CustomTable
          columns={columns}
          ref="table"
          url={`${config.payUrl}/api/acp/request/type/company/${
            this.props.match.params.setOfBooksId
          }/queryCompany?acpReqTypeId=${this.props.match.params.id}`}
        />
        <ListSelector
          visible={showListSelector}
          onCancel={() => this.handleListShow(false)}
          selectorItem={selectorItem}
          extraParams={{ acpReqTypesId: this.props.match.params.id }}
          onOk={this.handleListOk}
        />
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t('payment.return')}
          {/*返回*/}
        </a>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

const wrappedCompanyDistribution = Form.create()(AcpRequestTypesCompanyDistribution);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedCompanyDistribution);
