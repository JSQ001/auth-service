import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import { Row, Col, Button, Dropdown, Menu, Popover, Icon, message, Badge } from 'antd';
import moment from 'moment';
import config from 'config';
import service from './service';

class MyProjectApply extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          label: this.$t('contract.project.req.num'), // '项目申请单编号'
          type: 'input',
          id: 'projectReqNumber',
          colSpan: 6,
        },
        {
          label: this.$t('contract.project.name'), // 项目名称,
          type: 'input',
          id: 'projectName',
          colSpan: 6,
        },
        {
          label: this.$t('contract.project.flag'), // '是否立项',
          type: 'value_list',
          id: 'projectFlag',
          colSpan: 6,
          options: [
            { label: this.$t('common.yes'), value: true },
            { label: this.$t('common.no'), value: false },
          ],
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          label: this.$t('common.column.status'),
          type: 'value_list',
          id: 'status',
          colSpan: 6,
          options: [
            { label: this.$t('common.editing'), value: 1001 },
            { label: this.$t('common.approving'), value: 1002 },
            { label: this.$t('common.withdraw'), value: 1003 },
            { label: this.$t('common.approve.pass'), value: 1004 },
            { label: this.$t('common.approve.rejected'), value: 1005 },
          ],
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          label: this.$t('contract.project.leader'), // '项目负责人',
          id: 'pmId',
          type: 'list',
          listType: 'select_employee',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          single: true,
          colSpan: 6,
        },
        {
          label: this.$t('contract.project.number'), // '项目编号',
          type: 'input',
          id: 'projectNumber',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'startUseDate',
          items: [
            { type: 'date', id: 'startUseDateFrom', label: this.$t('contract.start.date.from') }, // '启用日期从'
            { type: 'date', id: 'startUseDateTo', label: this.$t('contract.start.date.to') }, // '启用日期至'
          ],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'closeUseDate',
          items: [
            { type: 'date', id: 'closeUseDateFrom', label: this.$t('contract.close.date.from') }, // '禁用日期从'
            { type: 'date', id: 'closeUseDateTo', label: this.$t('contract.close.date.to') }, // '禁用日期至'
          ],
          colSpan: 6,
        },
      ],
      searchParams: {}, // 搜索条件
      columns: [
        {
          title: this.$t('contract.project.req.num'),
          dataIndex: 'projectReqNumber',
          align: 'center',
          width: 240,
          tooltips: true,
        },
        {
          title: this.$t('contract.project.name'),
          dataIndex: 'projectName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('common.applicant'),
          dataIndex: 'employeeName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('common.apply.data'),
          dataIndex: 'requisitionDate',
          align: 'center',
          width: 150,
          render: data => {
            return (
              <Popover content={data ? moment(data).format('YYYY-MM-DD') : '-'}>
                {data ? moment(data).format('YYYY-MM-DD') : '-'}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.project.leader'),
          dataIndex: 'pmName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('contract.start.date'), // '启用日期',
          dataIndex: 'startUseDate',
          align: 'center',
          width: 150,
          render: data => {
            return (
              <Popover content={data ? moment(data).format('YYYY-MM-DD') : '-'}>
                {data ? moment(data).format('YYYY-MM-DD') : '-'}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.close.date'), // '禁用日期',
          dataIndex: 'closeUseDate',
          align: 'center',
          width: 150,
          render: data => {
            return (
              <Popover content={data ? moment(data).format('YYYY-MM-DD') : '-'}>
                {data ? moment(data).format('YYYY-MM-DD') : '-'}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.project.mark'), // '立项标志',
          dataIndex: 'projectFlag',
          align: 'center',
          width: 120,
          render: text => {
            return (
              <Popover
                content={text ? this.$t('contract.hasProject') : this.$t('contract.noProject')} // '已立项' : '未立项'
              >
                {text ? this.$t('contract.hasProject') : this.$t('contract.noProject')}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.approval.status'), // '审批状态',
          dataIndex: 'status',
          align: 'center',
          width: 120,
          render: status => {
            return (
              <Popover content={this.$statusList[status].label}>
                <Badge
                  status={this.$statusList[status].state}
                  text={this.$statusList[status].label}
                />
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.project.budget'), // '项目预算',
          dataIndex: 'operation',
          align: 'center',
          width: 150,
          fixed: 'right',
          render: (text, record) => {
            return (
              <a
                onClick={e => {
                  this.handleBudgetView(e, record);
                }}
              >
                {/* 预算 */}
                {this.$t('contract.budget')}
              </a>
            );
          },
        },
      ], // 表格列
      menuList: [], // 按钮组
    };
  }

  componentDidMount = () => {
    this.getProjectDefinedDetailsList();
  };

  //获取项目类型
  getProjectDefinedDetailsList = () => {
    const { company } = this.props;
    const params = {
      setOfBooksId: company.setOfBooksId,
    };
    service
      .getProjectDefinedDetails(params)
      .then(res => {
        this.setState({
          menuList: res.data,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 根据menuArr数据渲染下拉按钮组
  renderBtnMenu = menuList => {
    return (
      <Menu onClick={this.handleMenuToSave}>
        {menuList.map(menu => {
          return <Menu.Item key={menu.id}>{menu.projectReqTypeName}</Menu.Item>;
        })}
      </Menu>
    );
  };

  // 搜索域搜索
  handleSearch = values => {
    this.setState(
      {
        searchParams: {
          ...values,
          startUseDateFrom: values.startUseDateFrom
            ? moment(values.startUseDateFrom).format('YYYY-MM-DD')
            : '',
          startUseDateTo: values.startUseDateTo
            ? moment(values.startUseDateTo).format('YYYY-MM-DD')
            : '',
          closeUseDateFrom: values.closeUseDateFrom
            ? moment(values.closeUseDateFrom).format('YYYY-MM-DD')
            : '',
          closeUseDateTo: values.closeUseDateTo
            ? moment(values.closeUseDateTo).format('YYYY-MM-DD')
            : '',
        },
      },
      () => {
        // 调用查询方法
        const { searchParams } = this.state;
        this.table.search(searchParams);
      }
    );
  };

  // 重置搜索
  handleClear = () => {
    this.setState({ searchParams: {} }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
  };

  // 跳转新建页面
  handleMenuToSave = target => {
    console.log(target);
    const { dispatch } = this.props;
    const { menuList } = this.state;
    const isView = menuList[target.item.props.index].amountFlag;
    const isReq = menuList[target.item.props.index].amountNullFlag;
    dispatch(
      routerRedux.push({
        pathname: `/project-manage/my-project-apply/new-project-apply/${
          target.key
        }/${isView}/${isReq}`,
      })
    );
  };

  // 预算
  handleBudgetView = (e, record) => {
    e.preventDefault();
    e.stopPropagation();
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: `/project-manage/my-project-apply/budget-details/${record.id}`,
      })
    );
  };

  // 行点击跳转
  handleRowClick = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: `/project-manage/my-project-apply/project-apply-details/${record.id}`,
      })
    );
  };

  render() {
    const { searchForm, columns, menuList } = this.state;
    const { user } = this.props;
    const menu = this.renderBtnMenu(menuList);

    return (
      <div className="project-apply">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.handleClear}
          maxLength={4}
        />
        <Row style={{ padding: '10px 0' }}>
          <Col span={12}>
            <Dropdown
              overlay={menu}
              trigger={['click']}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              <Button type="primary">
                {/* 新建项目申请单 */}
                {this.$t('contract.projectApply.new')}
                <Icon type="down" />
              </Button>
            </Dropdown>
          </Col>
        </Row>
        <CustomTable
          columns={columns}
          ref={ref => {
            this.table = ref;
          }}
          onClick={this.handleRowClick}
          url={`${config.contractUrl}/api/project/requisition/query/by/cond?createdBy=${user.id}`}
          scroll={{ x: 1500 }}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}
export default connect(mapStateToProps)(MyProjectApply);
