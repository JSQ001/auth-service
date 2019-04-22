import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, message, Icon, Popover, Modal, Form, Switch, Badge, Input } from 'antd';
import Table from 'widget/table';
const FormItem = Form.Item;
import businessTypeService from './business-type.service';
import SearchArea from 'widget/search-area';
import { messages } from 'utils/utils';

class BusinessType extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      systemFlag: {
        SYSTEM: this.$t('workbench.system.preset'), // 系统预置
        USER: this.$t('workbench.user.defined'), // 用户自定义
      },
      loading: false,
      data: [],
      searchParams: {},
      page: 0,
      pageSize: 10,
      columns: [
        {
          title: messages('workbench.businessType.businessTypeCode' /*业务类型代码*/),
          dataIndex: 'businessTypeCode',
          width: 225,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.businessTypeName' /*业务类型名称*/),
          dataIndex: 'businessTypeName',
          width: 414,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.systemFlag' /*创建方式*/),
          dataIndex: 'systemFlag',
          width: 219,
          render: value => this.state.systemFlag[value],
        },
        {
          title: messages('workbench.businessType.enabled') /*状态*/,
          dataIndex: 'enabled',
          width: 110,
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
          width: 107,
          align: 'center',
          render: (text, record) =>
            record.systemFlag === 'USER' ? (
              <span>
                <a onClick={() => this.editItem(record)}>{this.$t('workbench.edit')}</a>
                <span className="ant-divider" />
                <a onClick={() => this.handleDetail(record)}>{this.$t('workbench.details')}</a>
              </span>
            ) : (
              <span>
                <a onClick={() => this.handleDetail(record)}>{this.$t('workbench.details')}</a>
              </span>
            ),
        },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'businessTypeCode',
          label: messages('workbench.businessType.businessTypeCode' /*业务类型代码*/),
        },
        {
          type: 'input',
          id: 'businessTypeName',
          label: messages('workbench.businessType.businessTypeName' /*业务类型名称*/),
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
      openWindowFlag: false,
      buttonLoading: false,
      isEditor: false,
      editorRecord: [],
      businessTypeDetail: '/workbench/business-type/business-type/detail/:id/:tab', // 详情
    };
  }

  componentDidMount() {
    this.getList();
  }
  getList = () => {
    this.setState({ loading: true });
    const { searchParams, page, pageSize } = this.state;
    businessTypeService
      .queryBusinessType(page, pageSize, searchParams)
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
  //弹出框关闭
  onClose = flag => {
    this.setState({
      openWindowFlag: false,
      isEditor: false,
      editorRecord: {},
    });
    if (flag) {
      this.getList();
    }
  };
  // 创建
  createItem = () => {
    this.setState({
      openWindowFlag: true,
    });
  };
  // 编辑
  editItem = record => {
    businessTypeService
      .getBusinessTypeById(record.id)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            openWindowFlag: true,
            editorRecord: res.data,
            isEditor: true,
          });
        } else {
          message.error(
            messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
          );
        }
      })
      .catch(() => {
        message.error(
          messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
        );
      });
  };
  // 详情
  handleDetail = record => {
    let path = this.state.businessTypeDetail.replace(':id', record.id).replace(':tab', 'PROCEDURE');
    this.props.dispatch({
      type: 'workbench/setBusinessType',
      businessType: record,
    });
    this.props.dispatch(
      routerRedux.push({
        pathname: path,
        query: { id: record.id },
      })
    );
  };
  // 保存
  saveHandle = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ buttonLoading: true });
        const { isEditor, editorRecord } = this.state;
        let params = { ...editorRecord, ...values };
        if (isEditor) {
          params['id'] = editorRecord.id;
        } else {
          params['id'] = null;
        }
        businessTypeService
          .saveBusinessType(params)
          .then(res => {
            message.success(messages('common.save.success', { name: res.data.businessTypeName })); //保存成功
            this.setState({ buttonLoading: false });
            // 跳转到详情界面
            this.handleDetail(res.data);
          })
          .catch(e => {
            if (e.response) {
              message.error(
                messages('common.save.filed' /*保存失败*/) + '!' + e.response.data.message
              );
            } else {
              message.error(messages('common.operate.filed'));
            }
            this.setState({ buttonLoading: false });
          });
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
      openWindowFlag,
      buttonLoading,
      isEditor,
      editorRecord,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { getFieldDecorator } = this.props.form;
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
            <Button type="primary" onClick={this.createItem}>
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

        <Modal
          visible={openWindowFlag}
          width={600}
          title={
            isEditor
              ? this.$t('workbench.editing.business.type ')
              : this.$t('workbench.new.business.type')
          }
          destroyOnClose={true}
          onCancel={() => this.onClose(false)}
          onOk={this.saveHandle}
          confirmLoading={buttonLoading}
        >
          <FormItem {...formItemLayout} label={messages('workbench.businessType.businessTypeCode')}>
            {getFieldDecorator('businessTypeCode', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? editorRecord.businessTypeCode : null,
            })(
              <Input disabled={isEditor} placeholder={messages('common.please.enter')} /> // 业务类型代码
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={messages('workbench.businessType.businessTypeName')}>
            {getFieldDecorator('businessTypeName', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.enter'), //请输入
                },
              ],
              initialValue: isEditor ? editorRecord.businessTypeName : null,
            })(
              <Input placeholder={messages('common.please.enter')} /> // 业务类型名称
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={messages('workbench.businessType.enabled')}>
            {getFieldDecorator('enabled', {
              initialValue: isEditor ? editorRecord.enabled : true,
              valuePropName: 'checked',
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              /> // 状态
            )}
          </FormItem>
        </Modal>
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {};
}

const WrappedBusinessType = Form.create()(BusinessType);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedBusinessType);
