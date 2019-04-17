import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, message, Popover, Badge, Popconfirm } from 'antd';
import Table from 'widget/table';
import SearchArea from 'widget/search-area';
import { messages } from 'utils/utils';
import moment from 'moment';
import service from './service';

class DispatchRule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      businessTypeId: this.props.id,
      newUrl: '/workbench/business-type/dispatch/rule/:businessTypeId',
      detailUrl: '/workbench/business-type/dispatch/rule/detail/:businessTypeId/:id',
      loading: false,
      data: [],
      searchParams: {},
      page: 0,
      pageSize: 10,
      columns: [
        {
          title: messages('workbench.businessType.dispatch.rule.code' /*派工规则代码*/),
          dataIndex: 'ruleCode',
          width: 246,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.name' /*派工规则名称*/),
          dataIndex: 'ruleName',
          width: 373,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.date' /*有效日期*/),
          dataIndex: 'startDate',
          width: 263,
          render: (value, recode) => (
            <span>
              <Popover
                content={
                  moment(recode.startDate).format('YYYY-MM-DD') +
                  ' ~ ' +
                  (recode.endDate ? moment(recode.endDate).format('YYYY-MM-DD') : '')
                }
              >
                {moment(recode.startDate).format('YYYY-MM-DD') +
                  ' ~ ' +
                  (recode.endDate ? moment(recode.endDate).format('YYYY-MM-DD') : '')}
              </Popover>
            </span>
          ),
        },
        {
          title: messages('workbench.businessType.enabled') /*状态*/,
          dataIndex: 'enabled',
          width: 88,
          align: 'center',
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={enabled ? this.$t('workbench.enabled') : this.$t('workbench.disabled')}
            />
          ),
        },
        {
          title: messages('workbench.businessType.operator' /*操作*/),
          key: 'operation',
          width: 104,
          align: 'center',
          render: (text, record) => (
            <span>
              <a onClick={() => this.handleEdit(record)}>{this.$t('workbench.edit')}</a>
              {/*编辑*/}
              <span className="ant-divider" />
              <Popconfirm
                key={record.id + 'delete'}
                title={messages('common.confirm.delete' /*确定要删除吗？*/)}
                onConfirm={() => this.handleDelete(record.id)}
              >
                <a>{this.$t('workbench.delete')}</a>
                {/*删除*/}
              </Popconfirm>
            </span>
          ),
        },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'ruleCode',
          colSpan: 8,
          label: messages('workbench.businessType.dispatch.rule.code' /*派工规则代码*/),
        },
        {
          type: 'input',
          id: 'ruleName',
          colSpan: 8,
          label: messages('workbench.businessType.dispatch.rule.name' /*派工规则名称*/),
        },
        {
          type: 'select',
          id: 'enabled',
          colSpan: 8,
          label: messages('workbench.businessType.enabled') /*状态*/,
          options: [
            { label: this.$t('workbench.enabled'), value: true },
            { label: this.$t('workbench.disabled'), value: false },
          ] /*禁用*/ /*启用*/,
          labelKey: 'label',
          valueKey: 'value',
        },
      ],
      pagination: {
        total: 0,
      },
      isEditor: false,
      editorRecord: [],
      frameFlag: false,
    };
  }

  componentWillMount() {
    this.getList();
  }

  getList = () => {
    this.setState({ loading: true });
    const { searchParams, page, pageSize, businessTypeId } = this.state;
    service
      .queryRule(page, pageSize, businessTypeId, searchParams)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            loading: false,
            data: res.data,
            pagination: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: page + 1,
              pageSize: pageSize,
              onChange: this.onChangePaper,
              pageSizeOptions: ['10', '20', '30', '40'],
              showSizeChanger: true,
              onShowSizeChange: this.onChangePageSize,
              showQuickJumper: true,
              showTotal: (total, range) =>
                messages('common.show.total', {
                  range0: `${range[0]}`,
                  range1: `${range[1]}`,
                  total: total,
                }),
            },
          });
        } else {
          this.setState({ loading: false });
          message.error(
            messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
          );
        }
      })
      .catch(() => {
        this.setState({ loading: false });
        message.error(
          messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
        );
      });
  };

  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };

  //每页多少条
  onChangePageSize = (page, pageSize) => {
    if (page - 1 !== this.state.page || pageSize !== this.state.pageSize) {
      this.setState({ page: page - 1, pageSize: pageSize }, () => {
        this.getList();
      });
    }
  };

  // 搜索
  search = values => {
    values.startDate = values.startDate ? moment(values.startDate).format('YYYY-MM-DD') : null;
    values.endDate = values.endDate ? moment(values.endDate).format('YYYY-MM-DD') : null;
    this.setState({ searchParams: values, page: 0 }, () => {
      this.getList();
    });
  };
  // 清除
  clearFunction = () => {
    this.setState({ searchParams: {} });
  };

  // 新建
  createRule = () => {
    const { businessTypeId, newUrl } = this.state;
    let path = newUrl.replace(':businessTypeId', businessTypeId);
    this.props.dispatch(
      routerRedux.push({
        pathname: path,
        query: { businessTypeId: businessTypeId },
      })
    );
  };
  // 编辑
  handleEdit = record => {
    const { businessTypeId, detailUrl } = this.state;
    let path = detailUrl.replace(':businessTypeId', businessTypeId).replace(':id', record.id);
    this.props.dispatch(
      routerRedux.push({
        pathname: path,
      })
    );
  };
  // 删除
  handleDelete = id => {
    service
      .deleteRule(id)
      .then(res => {
        message.success(messages('common.operate.success')); //操作成功
        this.getList();
      })
      .catch(e => {
        if (e.response) {
          message.error(
            messages('common.operate.filed' /*操作失败*/) + '!' + e.response.data.message
          );
        } else {
          message.error(messages('common.operate.filed'));
        }
      });
  };

  render() {
    const { columns, data, loading, pagination, searchParams, searchForm } = this.state;
    return (
      <div className="header-title">
        <SearchArea
          maxLength={4}
          searchParams={searchParams}
          submitHandle={this.search}
          clearHandle={this.clearFunction}
          searchForm={searchForm}
        />
        <div className="table-header">
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.createRule}>
              {this.$t({ id: 'common.create' } /*新建*/)}
            </Button>
          </div>
        </div>
        <Table
          rowKey={record => record.id}
          columns={columns}
          dataSource={data}
          loading={loading}
          pagination={pagination}
          bordered
          size="middle"
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(DispatchRule);
