import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { connect } from 'dva';
import baseService from 'share/base.service';
import NewCentralized from './new-centralized-authorization';
import workflowService from 'containers/setting/workflow/workflow.service';
import { Button, message, Popover } from 'antd';
import moment from 'moment';

class Centralized extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          options: [],
          id: 'setOfBooksId',
          placeholder: '请选择',
          label: '账套',
          labelKey: 'setOfBooksName',
          valueKey: 'id',
          isRequired: true,
          event: 'SETOFBOOKSID',
          allowClear: false,
          colSpan: 6,
          defaultValue: props.company.setOfBooksId,
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'documentCategory',
          label: '单据大类',
          options: [],
          event: 'DOCUMENTCATEGORY',
        },
        {
          type: 'select',
          colSpan: '6',
          id: 'formId',
          label: '单据类型',
          disabled: true,
          options: [],
        },
        {
          type: 'list',
          options: [],
          label: '公司',
          id: 'companyId',
          colSpan: 6,
          listType: 'company',
          labelKey: 'name',
          // labelKey: '${companyCode}-${name}',
          valueKey: 'id',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'unitId',
          options: [],
          label: '部门',
          listType: 'department',
          labelKey: 'name',
          // labelKey: '${departmentCode}-${name}',
          valueKey: 'departmentId',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'mandatorId',
          label: '委托人',
          options: [],
          listType: 'select_authorization_user',
          // labelKey: 'userName',
          labelKey: '${userCode}-${userName}',
          valueKey: 'userId',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'baileeId',
          label: '受托人',
          options: [],
          listType: 'select_authorization_user',
          labelKey: '${userCode}-${userName}',
          // labelKey: 'userName',
          valueKey: 'userId',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
        },
        {
          type: 'date',
          colSpan: 3,
          id: 'startDate',
          label: '有效日期从',
        },
        {
          type: 'date',
          colSpan: 3,
          id: 'endDate',
          label: '有效日期至',
        },
      ],
      columns: [
        {
          title: '账套',
          dataIndex: 'setOfBooksName',
          align: 'center',
          width: 180,
          render: (setOfBooksName, record) => {
            return (
              <span>
                {record.setOfBooksCode}-{record.setOfBooksName}
              </span>
            );
          },
        },
        {
          title: '单据大类',
          dataIndex: 'documentCategoryDesc',
          align: 'center',
          width: 120,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '单据类型',
          dataIndex: 'formName',
          align: 'center',
          width: 110,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '公司',
          dataIndex: 'companyName',
          align: 'center',
          width: 100,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '部门',
          dataIndex: 'unitName',
          align: 'center',
          width: 100,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '委托人',
          dataIndex: 'mandatorName',
          align: 'center',
          width: 110,
          render: (mandatorName, record) => {
            return (
              <Popover content={record.mandatorName}>
                {record.mandatorCode}-{record.mandatorName}
              </Popover>
            );
          },
        },
        {
          title: '受托人',
          dataIndex: 'baileeName',
          align: 'center',
          width: 110,
          render: (baileeName, record) => {
            return (
              <Popover content={record.baileeName}>
                {record.baileeCode}-{record.baileeName}
              </Popover>
            );
          },
        },

        {
          title: '有效日期',
          dataIndex: 'date',
          align: 'center',
          width: 200,
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
          dataIndex: 'options',
          align: 'center',
          width: 60,
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
      setOfBooksId: props.company.setOfBooksId,
      fromList: [],
    };
  }

  // 生命周期
  componentDidMount() {
    this.getSetOfBookList();
    this.getBillList();
  }

  //获取账套列表
  getSetOfBookList = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        let list = [];
        res.data.map(item => {
          list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
        });
        let form = this.state.searchForm;
        form[0].options = list;
        this.setState({ searchForm: form });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ saveLoading: false });
      });
  };

  //  获取单据大类
  getBillList = () => {
    this.getSystemValueList('SYS_APPROVAL_FORM_TYPE')
      .then(res => {
        let list = [];
        res.data.values.map(item => {
          list.push({ value: item.value, label: `${item.name}` });
        });
        let form = this.state.searchForm;
        form[1].options = list;
        this.setState({ searchForm: form });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 新建
  createCentralized = () => {
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
    this.setState({
      setOfBooksId: value.setOfBooksId,
    });
    let params = {
      ...value,
      startDate: value.startDate && value.startDate.format('YYYY-MM-DD'),
      endDate: value.endDate && value.endDate.format('YYYY-MM-DD'),
    };
    this.table.search(params);
  };

  clear = () => {
    const { setOfBooksId } = this.state.setOfBooksId;
    let form = this.state.searchForm;
    if (setOfBooksId) {
      form[0].defaultValue = setOfBooksId;
    } else {
      form[0].defaultValue = this.props.company.setOfBooksId;
    }
    this.setState({ searchForm: form, setOfBooksId: form[0].defaultValue });
    this.state.searchParams = {};
    // this.table.search();
    this.handleEvent();
  };

  // 关闭
  handleCloseSlide = flag => {
    this.setState({ showSlideFrame: false }, () => {
      flag && this.table.search(this.state.searchParams);
    });
  };

  handleEvent = (event, value, record) => {
    let newSearchItem = this.state.searchForm;
    let list = [];
    switch (event) {
      case 'SETOFBOOKSID': //账套id事件
        newSearchItem[3].listExtraParams = newSearchItem[4].listExtraParams = newSearchItem[5].listExtraParams = newSearchItem[6].listExtraParams = {
          setOfBooksId: value,
        };
        this.setState(
          {
            setOfBooksId: value,
            searchParams: { ...this.state.searchParams, setOfBooksId: value },
          }
          // () => {
          //   this.table.search(this.state.searchParams);
          // }
        );
        break;
      case 'DOCUMENTCATEGORY': //单据大类事件
        newSearchItem[2].disabled = value ? false : true;
        newSearchItem[2].options = [];
        this.searchAreaRef && this.searchAreaRef.setValues({ formId: '' });
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
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else if (value === '801003') {
          //预付款单类型定义
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
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else if (value === '801006') {
          //费用调整单类型定义
          httpFetch
            .get(
              `${config.expenseUrl}/api/expense/adjust/types/query?page=0&size=1000&setOfBooksId=` +
                this.state.setOfBooksId
            )
            .then(({ data }) => {
              data.map(item => {
                list.push({ value: item.id, label: `${item.expAdjustTypeName}` });
              });
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else if (value === '801005') {
          //付款申请单类型定义
          httpFetch
            .get(
              `${config.payUrl}/api/acp/request/type/query?page=0&size=1000&setOfBooksId=` +
                this.state.setOfBooksId
            )
            .then(({ data }) => {
              data.map(item => {
                list.push({ value: item.id, label: `${item.typeName}` });
              });
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else if (value === '801004') {
          //合同类型定义
          httpFetch
            .get(
              `${config.contractUrl}/api/contract/type/query?page=0&size=1000&setOfBooksId=` +
                this.state.setOfBooksId
            )
            .then(({ data }) => {
              data.map(item => {
                list.push({ value: item.id, label: `${item.contractTypeName}` });
              });
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else if (value === '801009') {
          //费用申请单类型定义
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
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else if (value === '801002') {
          //预算日记账
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
              newSearchItem[2].options = list;
              this.setState({ searchForm: newSearchItem });
            })
            .catch(err => {
              message.error(err.response.message);
            });
        } else {
          newSearchItem[2].options = [];
          this.setState({ searchForm: newSearchItem });
        }
        break;
    }
  };

  render() {
    const {
      searchForm,
      columns,
      showSlideFrame,
      updateParams,
      setOfBooksId,
      fromList,
    } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.handleEvent}
          maxLength={4}
          onRef={ref => (this.searchAreaRef = ref)}
        />
        <Button
          style={{ margin: '15px 0' }}
          className="create-btn"
          type="primary"
          onClick={this.createCentralized}
        >
          新 建
        </Button>
        <CustomTable
          columns={columns}
          url={`${
            config.mdataUrl
          }/api/authorize/form/centralized/auth/pageByCondition?setOfBooksId=${setOfBooksId}`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={JSON.stringify(updateParams) === '{}' ? '新建集中授权' : '编辑集中授权'}
          show={showSlideFrame}
          onClose={() => this.setState({ showSlideFrame: false })}
        >
          <NewCentralized
            params={{ ...updateParams }}
            close={this.handleCloseSlide}
            setOfBooks={searchForm[0].options}
            setOfBills={searchForm[1].options}
            fromList={fromList}
            setOfBooksId={setOfBooksId}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    tenantMode: true,
    user: state.user.currentUser,
    company: state.user.company,
    organization: state.user.organization || {},
  };
}

export default connect(mapStateToProps)(Centralized);
