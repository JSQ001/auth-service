/* eslint-disable */
import React from 'react';
import { message, Input, Row, Col } from 'antd';
import config from 'config';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
const Search = Input.Search;
import httpFetch from 'share/httpFetch';
import { connect } from 'dva';
import moment from 'moment';
import { routerRedux } from 'dva/router';

class ApprovalToPend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          colSpan: 6,
          id: 'documentCategory',
          label: '单据大类',
          options: [],
          defaultValue: props.documentType === '0' ? '' : props.documentType,
          event: 'change',
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'documentTypeId',
          label: '单据类型',
          options: [],
          disabled: true,
        },
        {
          type: 'input',
          id: 'applicantName',
          label: this.$t('acp.employeeName' /*申请人*/),
          options: [],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'submitDate',
          items: [
            { type: 'date', id: 'beginDate', label: '提交日期从' },
            { type: 'date', id: 'endDate', label: '提交日期至' },
          ],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'functionAmount',
          items: [
            {
              type: 'inputNumber',
              id: 'amountFrom',
              label: '本币金额从',
            },
            {
              type: 'inputNumber',
              id: 'amountTo',
              label: '本币金额至',
            },
          ],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'remark',
          label: '备注',
          colSpan: 6,
        },
      ],
      columns: [
        {
          dataIndex: 'documentNumber',
          title: '单据编号',
          align: 'center',
        },
        {
          dataIndex: 'documentCategoryName',
          title: '单据大类',
          align: 'center',
        },
        {
          dataIndex: 'documentTypeName',
          title: '单据类型',
          align: 'center',
        },
        {
          dataIndex: 'applicantName',
          title: '申请人',
          align: 'center',
        },
        {
          dataIndex: 'submittedDate',
          title: '提交日期',
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD HH:mm:ss'),
        },
        {
          dataIndex: 'functionAmount',
          title: '本币金额',
          align: 'center',
        },
        {
          dataIndex: 'applicantDate',
          title: '到达时间',
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD HH:mm:ss'),
        },
        {
          dataIndex: 'remark',
          title: '备注',
          align: 'center',
        },
      ],
      setOfBooksId: this.props.company.setOfBooksId,
      total: [],
      searchParams: {},
    };
  }

  componentWillMount() {
    this.getTotal();
  }

  // 获取分类信息
  getTotal = params => {
    let list = [];
    return httpFetch
      .get(`${config.workflowUrl}/api/workflow/getApprovalToPendTotal`, params)
      .then(response => {
        response.data.map(item => {
          list.push({ name: `${item.name}`, count: `${item.count}`, type: item.type });
        });
        this.setState({
          total: list,
        });
      });
  };

  // 生命周期
  componentDidMount() {
    this.getBillList();
    const { documentType } = this.props;
    if (documentType !== '0') {
      this.handleEvent('change', documentType);
    }
  }

  // 获取单据大类
  getBillList = () => {
    this.getSystemValueList('SYS_APPROVAL_FORM_TYPE')
      .then(res => {
        let list = [];
        res.data.values.map(item => {
          list.push({ value: item.value, label: `${item.name}` });
        });
        let form = this.state.searchForm;
        form[0].options = list;
        this.setState({ searchForm: form });
      })
      .catch(err => {
        message.error(err.response.message);
      });
  };

  // 搜索框事件
  handleEvent = (event, value) => {
    let newSearchItem = this.state.searchForm,
      list = [];
    if (event && value) {
      newSearchItem[1].disabled = false;
    } else {
      newSearchItem[1].disabled = true;
    }
    // 根据单据大类的value值获取各个单据类型的数据
    if (value === '801008') {
      //核算工单
      httpFetch
        .get(
          `${
            config.accountingUrl
          }/api/general/ledger/work/order/types/query?page=0&size=1000&setOfBooksId=` +
            this.state.setOfBooksId
        )
        .then(({ data }) => {
          data.map(item => {
            list.push({ value: item.id, label: `${item.workOrderTypeName}` });
          });
          newSearchItem[1].options = list;
          this.setState({ searchForm: newSearchItem });
        })
        .catch(err => {
          message.error(err.response.message);
        });
    } else {
      newSearchItem[1].options = [];
      this.setState({ searchForm: newSearchItem });
    }
    switch (value) {
      case '801005': //付款申请单类型定义
        httpFetch
          .get(
            `${config.payUrl}/api/acp/request/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.typeName}` });
            });
            newSearchItem[1].options = list;
            this.setState({ searchForm: newSearchItem });
          })
          .catch(err => {
            message.error(err.response.message);
          });
        break;
      case '801006': //费用调整单类型定义
        httpFetch
          .get(
            `${config.expenseUrl}/api/expense/adjust/types/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.expAdjustTypeName}` });
            });
            newSearchItem[1].options = list;
            this.setState({ searchForm: newSearchItem });
          })
          .catch(err => {
            message.error(err.response.message);
          });
        break;
      case '801003': //预付款单类型定义
        httpFetch
          .get(
            `${
              config.prePaymentUrl
            }/api/cash/pay/requisition/types/query?&page=0&size=1000&setOfBookId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.typeName}` });
            });
            newSearchItem[1].options = list;
            this.setState({ searchForm: newSearchItem });
          })
          .catch(err => {
            message.error(err.response.message);
          });
        break;
      case '801004': //合同类型定义
        httpFetch
          .get(
            `${config.contractUrl}/api/contract/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.contractTypeName}` });
            });
            newSearchItem[1].options = list;
            this.setState({ searchForm: newSearchItem });
          })
          .catch(err => {
            message.error(err.response.message);
          });
        break;
      case '801009': //费用申请单类型定义
        httpFetch
          .get(
            `${
              config.expenseUrl
            }/api/expense/application/type/query?page=0&size=1000&setOfBooksId=` +
              this.state.setOfBooksId
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.typeName}` });
            });
            newSearchItem[1].options = list;
            this.setState({ searchForm: newSearchItem });
          })
          .catch(err => {
            message.error(err.response.message);
          });
        break;
      case '801002': //预算日记账
        httpFetch
          .get(
            `${
              config.budgetUrl
            }/api/budget/journals/query/headers/byInput?journalTypeId=&journalCode=&periodStrategy=&status=&page=0&size=1000&journalTypeId=` +
              this.props.organization.id
          )
          .then(({ data }) => {
            data.map(item => {
              list.push({ value: item.id, label: `${item.journalTypeName}` });
            });
            newSearchItem[1].options = list;
            this.setState({ searchForm: newSearchItem });
          })
          .catch(err => {
            message.error(err.response.message);
          });
        break;
    }
  };

  //搜索
  search = values => {
    let params = { ...this.state.searchParams, ...values };

    if (params.amountFrom > params.amountTo) {
      message.error('金额从不应大于金额至');
    }

    params.beginDate && (params.beginDate = moment(params.beginDate).format('YYYY-MM-DD'));
    params.endDate && (params.endDate = moment(params.endDate).format('YYYY-MM-DD'));

    this.setState({ searchParams: params }, () => {
      this.table.search(this.state.searchParams);
      this.getTotal(this.state.searchParams);
    });
  };

  //重置
  reset = () => {
    let newSearchItem = this.state.searchForm;
    newSearchItem[1].disabled = true;
    newSearchItem[1].options = [];
    this.setState({ searchForm: newSearchItem });
    this.getTotal();
    this.table.reload();
  };

  //根据单号查询
  searchNumber = e => {
    this.setState({ searchParams: { documentNumber: e } }, () => {
      this.table.search(this.state.searchParams);
      this.getTotal(this.state.searchParams);
    });
  };

  //行点击事件
  rowClick = record => {
    let path = '';
    switch (record.documentCategory) {
      case 801009: //费用申请单
        path = '/approval-management/expense-application-approve/detail/:status/:id'
          .replace(':id', record.documentId)
          .replace(':status', 'unapproved');
        break;
      case 801003: //预付款单
        path = `/approval-management/pre-payment-approve/pre-payment-approve-detail/${
          record.documentId
        }/${record.entityOid}/unapproved`;
        break;
      case 801002: //预算日记账
        path = `/approval-management/budget-journal-check/budget-journal-check-detail/${
          record.documentId
        }/${record.documentNumber}/unapproved`;
        break;
      case 801008: //核算工单
        path = `/approval-management/gl-work-order-approval/gl-work-order-approval-detail/${
          record.documentId
        }/${record.entityOid}/${record.status}`;
        break;
      case 801011: //项目申请单
        path = '/approval-management/approval-project/detail/:status/:id/:oId'
          .replace(':id', record.documentId)
          .replace(':status', 'unapproved')
          .replace(':oId', record.entityOid);
        break;
      case 801010: //差旅申请单
        path = '/approval-management/travel-application-approve/detail/:status/:id'
          .replace(':id', record.documentId)
          .replace(':status', 'unapproved');
        break;
      case 801004: //合同
        path = `/approval-management/contract-approve/contract-workflow-approve-detail/${
          record.documentId
        }/${record.entityOid}/${record.documentCategory}/unapproved`;
        break;
      case 801001: //对公报账单
        path = `/approval-management/approval-my-reimburse/approve-reimburse-detail/${
          record.documentId
        }/${record.entityOid}/unapproved`;
        break;
      case 801005: //付款申请单
        path = '/approval-management/payment-requisition/detail/:id/:entityOid/true'
          .replace(':id', record.documentId)
          .replace(':entityOid', record.entityOid);
        break;
      case 801006: //费用调整单
        path = '/approval-management/approve-expense-adjust/expense-adjust-approve-detail/:expenseAdjustTypeId/:id/:entityOid/:flag/:entityType'
          .replace(':id', record.documentId)
          .replace(':expenseAdjustTypeId', record.documentTypeId)
          .replace(':entityOid', record.entityOid)
          .replace(':flag', 'unapproved')
          .replace(':entityType', record.documentCategory);
        break;
      case 901001: //开户申请
        path = '';
        break;
      case 901002: //开户维护
        path = '';
        break;
      case 901003: //账户变更
        path = '';
        break;
      case 901004: //手工付款
        path = '';
        break;
      case 901005: //批量付款
        path = '';
        break;
      case 902001: //税务登记新增
        path = '/approval-management/registration-approve/detail/:status/:id'
          .replace(':id', record.documentId)
          .replace(':status', 'unapproved');
        break;
      case 904001: //客户信息新增
        path = '/inter-management/cust-inter/customer-inter-detail/' + record.documentId;
        break;
    }
    this.props.dispatch(
      routerRedux.push({
        pathname: path,
      })
    );
  };

  render() {
    const { searchForm, columns, total } = this.state;
    const { documentType } = this.props;
    return (
      <div className="approval">
        <SearchArea
          maxLength={4}
          submitHandle={this.search}
          clearHandle={this.reset}
          searchForm={searchForm}
          eventHandle={this.handleEvent}
        />
        <div style={{ margin: '10px 10px' }}>
          <Row>
            <Col id="payment-requisition-drop" style={{ position: 'relative' }} span={18}>
              {total &&
                total.map(item => {
                  return (
                    <div key={item.type}>
                      <Col span={3} style={{ fontWeight: 'bold' }}>
                        {item.name}:
                      </Col>
                      <Col span={1} style={{ color: '#008000', fontWeight: 'bold' }}>
                        {item.count}笔{' '}
                      </Col>
                    </div>
                  );
                })}
            </Col>
            <Col span={6}>
              <Search placeholder="请输入单据编号" onSearch={this.searchNumber} enterButton />
            </Col>
          </Row>
        </div>
        <CustomTable
          tableKey="documentId"
          columns={columns}
          url={`${config.workflowUrl}/api/workflow/getApprovalToPendList`}
          ref={ref => (this.table = ref)}
          params={{ documentCategory: documentType === '0' ? '' : documentType }}
          onClick={this.rowClick}
          onRowClick={this.rowClick}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    tenantMode: true,
    company: state.user.company,
    organization: state.user.organization || {},
  };
}

export default connect(mapStateToProps)(ApprovalToPend);
