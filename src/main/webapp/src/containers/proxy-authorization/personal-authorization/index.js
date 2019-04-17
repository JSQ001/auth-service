import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { connect } from 'dva';
import NewPersonal from './new-personal-authorization';
import { Button, message } from 'antd';
import moment from 'moment';

class Personal extends Component {
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
          event: 'change',
        },
        {
          type: 'select',
          colSpan: '6',
          id: 'formId',
          label: '单据类型',
          options: [],
          disabled: true,
        },
        {
          type: 'list',
          colSpan: '6',
          id: 'baileeId',
          label: '受托人',
          listType: 'select_authorization_user',
          labelKey: '${userCode}-${userName}',
          // labelKey: 'userName',
          valueKey: 'userId',
          event: 'APPLIER',
          single: true,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'date',
          items: [
            { type: 'date', id: 'startDate', colSpan: 3, label: '有效日期从' },
            { type: 'date', id: 'endDate', colSpan: 3, label: '有效日期至' },
          ],
        },
      ],
      columns: [
        {
          title: '委托人',
          dataIndex: 'mandatorName',
          align: 'center',
          width: 180,
          render: (baileeName, record) => {
            return (
              <span>
                {record.mandatorCode}-{record.mandatorName}
              </span>
            );
          },
        },
        { title: '单据大类', dataIndex: 'documentCategoryDesc', align: 'center' },
        { title: '单据类型', dataIndex: 'formName', align: 'center' },
        {
          title: '受托人',
          dataIndex: 'baileeName',
          align: 'center',
          width: 180,
          render: (baileeName, record) => {
            return (
              <span>
                {record.baileeCode}-{record.baileeName}
              </span>
            );
          },
        },
        {
          title: '有效日期',
          dataIndex: 'date',
          align: 'center',
          width: 250,
          render: (text, record, index) => {
            return (
              <span>
                {moment(record.startDate).format('YYYY-MM-DD')}~{record.endDate
                  ? moment(record.endDate).format('YYYY-MM-DD')
                  : ''}
              </span>
            );
          },
        },
        {
          title: '操作',
          dataIndex: 'id',
          align: 'center',
          width: 100,
          render: (value, record, index) => {
            return (
              <span>
                <a
                  onClick={() => {
                    this.edit(record);
                  }}
                >
                  编辑
                </a>
              </span>
            );
          },
        },
      ],
      updateParams: {},
      searchParams: {},
      showSlideFrame: false,
      setOfBooksId: this.props.company.setOfBooksId,
    };
  }

  // 生命周期
  componentDidMount() {
    this.getBillList();
  }

  //  获取单据大类
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
  // 新建
  createPersonal = () => {
    this.setState({
      updateParams: {},
      showSlideFrame: true,
    });
  };

  // 编辑
  edit = record => {
    this.setState({
      showSlideFrame: true,
      updateParams: JSON.parse(JSON.stringify(record)),
    });
  };

  // 搜索
  search = value => {
    let params = {
      ...value,
      startDate: value.startDate && value.startDate.format('YYYY-MM-DD'),
      endDate: value.endDate && value.endDate.format('YYYY-MM-DD'),
    };
    this.table.search(params);
  };

  clear = () => {
    this.handleEvent();
    this.setState({ searchParams: {} });
    this.table.search();
  };

  // 关闭
  handleCloseSlide = flag => {
    this.setState({ showSlideFrame: false }, () => {
      flag && this.table.search(this.state.searchParams);
    });
  };

  render() {
    const { searchForm, columns, showSlideFrame, updateParams } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.handleEvent}
        />
        <Button
          style={{ margin: '15px 0' }}
          className="create-btn"
          type="primary"
          onClick={this.createPersonal}
        >
          新 建
        </Button>
        <CustomTable
          columns={columns}
          url={`${config.mdataUrl}/api/authorize/form/personal/auth/pageByCondition`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={JSON.stringify(updateParams) === '{}' ? '新建授权' : '编辑授权'}
          show={showSlideFrame}
          onClose={() => this.setState({ showSlideFrame: false })}
        >
          <NewPersonal
            setOfBooks={searchForm[0].options}
            setOfTypes={searchForm[1].options}
            params={{ ...updateParams }}
            close={this.handleCloseSlide}
          />
        </SlideFrame>
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

export default connect(mapStateToProps)(Personal);
