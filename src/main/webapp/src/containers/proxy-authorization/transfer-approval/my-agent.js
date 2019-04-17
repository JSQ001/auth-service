import React, { Component } from 'react';
import workflowService from 'containers/setting/workflow/workflow.service';
import { connect } from 'dva';
import { message, Popover } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import config from 'config';
import moment from 'moment';

class MyAgent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          colSpan: 6,
          id: 'documentCategory',
          label: '单据大类',
          event: 'DOV',
          options: [{ label: 'demo', value: 'demo' }],
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'workflowId',
          label: '审批流',
          disabled: true,
          options: [],
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'authorizeId',
          label: '授权人',
          listType: 'select_authorization_user',
          event: 'APPLIER',
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
        { title: '单据大类', dataIndex: 'documentCategoryName', align: 'center', width: 170 },
        { title: '审批流', dataIndex: 'workflowName', align: 'center', width: 170 },
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
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
      ],
      searchParams: {},
    };
  }

  // 生命周期
  componentDidMount() {
    this.getBillList();
  }

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
    workflowService.getWorkflowList(params).then(({ data }) => {
      let list = [];
      data.map(item => {
        list.push({ value: item.id, label: `${item.formName}` });
      });
      newSearchItem[1].options = list;
      this.setState({
        searchForm: newSearchItem,
      });
    });
  };

  render() {
    const { searchForm, columns } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.handleEvent}
          maxLength={5}
        />
        <br />
        <br />
        <CustomTable
          columns={columns}
          url={`${config.workflowUrl}/api/workflow/transfer/query?tab=agent`}
          ref={ref => (this.table = ref)}
        />
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
)(MyAgent);
