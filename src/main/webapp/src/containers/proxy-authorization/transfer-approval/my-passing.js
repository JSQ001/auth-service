import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import workflowService from 'containers/setting/workflow/workflow.service';
import config from 'config';
import { connect } from 'dva';
import moment from 'moment';
import NewMyPassing from './new-my-passing';
import { Button, message, Popover } from 'antd';

class MyPassing extends Component {
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
          event: 'APPLIER',
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'workflowId',
          disabled: true,
          label: '审批流',
          options: [],
        },
        {
          type: 'list',
          options: [],
          label: '代理人',
          id: 'agentId',
          colSpan: 6,
          listType: 'select_authorization_user',
          labelKey: '${userCode}-${userName}',
          // labelKey: 'userName',
          valueKey: 'userId',
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
        {
          type: 'input',
          colSpan: 6,
          id: 'authorizationNotes',
          label: '备注',
        },
      ],
      columns: [
        {
          title: '授权人',
          dataIndex: 'authorizerName',
          align: 'center',
          width: 140,
          render: (authorizerName, record) => {
            return (
              <span>
                {record.authorizerCode}-{record.authorizerName}
              </span>
            );
          },
        },
        { title: '单据大类', dataIndex: 'documentCategoryName', align: 'center', width: 150 },
        { title: '审批流', dataIndex: 'workflowName', align: 'center', width: 150 },
        {
          title: '代理人',
          dataIndex: 'agentName',
          align: 'center',
          width: 150,
          render: (agentName, record) => {
            return (
              <span>
                {record.agentCode}-{record.agentName}
              </span>
            );
          },
        },
        {
          title: '有效日期',
          dataIndex: 'date',
          align: 'center',
          width: 220,
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
          title: '备注',
          dataIndex: 'authorizationNotes',
          align: 'center',
          width: 190,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: '操作',
          dataIndex: 'id',
          align: 'center',
          width: 90,
          render: (value, record, index) => {
            return (
              <span>
                <a
                  onClick={() => {
                    this.edit(record);
                  }}
                >
                  {' '}
                  编辑{' '}
                </a>
              </span>
            );
          },
        },
      ],
      updateParams: {},
      searchParams: {},
      showSlideFrame: false,
      documentType: [],
      saveLoading: false,
      newSearchItem: {},
      billValue: {},
      flowList: [],
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
    let newSearchItem = this.state.searchForm;
    if (event && value) {
      newSearchItem[1].disabled = false;
    } else {
      newSearchItem[1].disabled = true;
    }
    let params = {
      documentCategory: value,
      booksID: this.props.tenantMode ? this.props.company.setOfBooksId : '',
    };
    workflowService
      .getWorkflowList(params)
      .then(({ data }) => {
        let list = [];
        data.map(item => {
          list.push({ value: item.id, label: `${item.formName}` });
        });
        newSearchItem[1].options = list;
        this.setState({
          searchForm: newSearchItem,
        });
      })
      .catch(err => {
        message.error(err.response.message);
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
    const { searchForm, columns, showSlideFrame, updateParams, flowList } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.handleEvent}
          maxLength={5}
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
          url={`${config.workflowUrl}/api/workflow/transfer/query?tab=authorizer`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={JSON.stringify(updateParams) === '{}' ? '新建转交' : '编辑转交'}
          show={showSlideFrame}
          onClose={() => this.setState({ showSlideFrame: false })}
        >
          <NewMyPassing
            flowList={flowList}
            params={{ ...updateParams }}
            close={this.handleCloseSlide}
            setOfBooks={searchForm[0].options}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    tenantMode: true,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(MyPassing);
