import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, message, Popover, Badge, Popconfirm } from 'antd';
import Table from 'widget/table';
import SearchArea from 'widget/search-area';
import SlideFrame from 'widget/slide-frame';
import { messages } from 'utils/utils';
import businessPageService from './business-page.service';
import BusinessPageDetail from './business-page-detail';

class BusinessPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      systemFlag: {
        SYSTEM: this.$t('workbench.system.preset'), // 系统预置
        USER: this.$t('workbench.user.defined'), // 用户自定义
      },
      businessTypeId: this.props.id,
      loading: false,
      data: [],
      searchParams: {},
      page: 0,
      pageSize: 10,
      columns: [
        {
          title: messages('workbench.businessType.page.code' /*页面代码*/),
          dataIndex: 'pageCode',
          width: 234,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.page.name' /*页面名称*/),
          dataIndex: 'pageName',
          width: 180,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.page.content' /*页面*/),
          dataIndex: 'pageContent',
          width: 358,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.enabled') /*状态*/,
          dataIndex: 'enabled',
          width: 87,
          align: 'center',
          render: enabled => (
            <Badge status={enabled ? 'success' : 'error'} text={enabled ? '启用' : '禁用'} />
          ),
        },
        {
          title: messages('workbench.businessType.systemFlag' /*创建方式*/),
          dataIndex: 'systemFlag',
          width: 106,
          render: value => this.state.systemFlag[value],
        },
        {
          title: messages('workbench.businessType.operator' /*操作*/),
          key: 'operation',
          width: 111,
          align: 'center',
          render: (text, record) =>
            record.systemFlag === 'USER' ? (
              <span>
                <a onClick={() => this.handleEdit(record, false)}>{this.$t('workbench.edit')}</a>
                <span className="ant-divider" />
                <Popconfirm
                  key={record.id + 'delete'}
                  title={messages('common.confirm.delete' /*确定要删除吗？*/)}
                  onConfirm={() => this.handleDelete(record.id)}
                >
                  <a>{this.$t('workbench.delete')}</a>
                </Popconfirm>
              </span>
            ) : (
              <span>
                <a onClick={() => this.handleEdit(record, true)}>{this.$t('workbench.details')}</a>
              </span>
            ),
        },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'pageCode',
          label: messages('workbench.businessType.page.code' /*页面代码*/),
        },
        {
          type: 'input',
          id: 'pageName',
          label: messages('workbench.businessType.page.name' /*页面名称*/),
        },
        {
          type: 'select',
          id: 'enabled',
          label: messages('workbench.businessType.enabled') /*状态*/,
          options: [
            { label: this.$t('workbench.enabled'), value: true },
            { label: this.$t('workbench.disabled'), value: false },
          ],
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

  componentDidMount() {
    this.getList();
  }

  getList = () => {
    this.setState({ loading: true });
    const { searchParams, page, pageSize, businessTypeId } = this.state;
    businessPageService
      .queryBusinessPage(page, pageSize, businessTypeId, searchParams)
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
    this.setState({ searchParams: values, page: 0 }, () => {
      this.getList();
    });
  };
  // 清除
  clearFunction = () => {
    this.setState({ searchParams: {} });
  };

  // 新建
  createPage = () => {
    this.setState({
      frameFlag: true,
      editorRecord: {},
      frameTitle: '', // 新增操作页面
      isEditor: false,
    });
  };
  // 编辑
  handleEdit = (record, isView) => {
    let title;
    if (isView) {
      title = this.$t('workbench.add.operation.page'); // 操作页面详情
    } else {
      title = this.$t('workbench.edit.operation.page'); // 编辑操作页面
    }
    this.setState({
      frameFlag: true,
      editorRecord: record,
      frameTitle: title,
      isEditor: true,
    });
  };
  // 删除
  handleDelete = id => {
    businessPageService
      .deleteBusinessPage(id)
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
  //侧滑窗口关闭
  cancelWindow = flag => {
    this.setState({ frameFlag: false }, () => {
      if (flag === true) {
        this.getList();
      }
    });
  };

  render() {
    const {
      columns,
      data,
      loading,
      pagination,
      searchParams,
      searchForm,
      frameFlag,
      editorRecord,
      frameTitle,
      isEditor,
      businessTypeId,
    } = this.state;
    return (
      <div className="header-title">
        <SearchArea
          maxLength={3}
          searchParams={searchParams}
          submitHandle={this.search}
          clearHandle={this.clearFunction}
          searchForm={searchForm}
        />
        <div className="table-header">
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.createPage}>
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
        <SlideFrame title={frameTitle} show={frameFlag} onClose={e => this.cancelWindow(e)}>
          <BusinessPageDetail
            onClose={e => this.cancelWindow(e)}
            params={{ record: editorRecord, isEditor: isEditor, businessTypeId: businessTypeId }}
          />
        </SlideFrame>
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
)(BusinessPage);
