import React from 'react';
import { connect } from 'dva';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { Form, Tag, Input, Button, Modal, message, Row, Col } from 'antd';
import { messages } from 'utils/utils';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import PropTypes from 'prop-types';
const FormItem = Form.Item;
const TextArea = Input.TextArea;

class Notify extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'keyContact',
          label: '员工代码、名称',
        },
        {
          type: 'input',
          id: 'keyCompany',
          label: '公司代码、名称',
        },
        {
          type: 'input',
          id: 'keyDepartment',
          label: '部门代码、名称',
        },
      ],
      columns: [
        {
          title: '员工代码',
          dataIndex: 'employeeId',
          width: 100,
          tooltips: true,
        },
        {
          title: '员工名称',
          dataIndex: 'fullName',
          width: 100,
          tooltips: true,
        },
        {
          title: '公司',
          dataIndex: 'companyName',
          width: 150,
          tooltips: true,
        },
        {
          title: '部门',
          dataIndex: 'departmentName',
          width: 150,
          tooltips: true,
        },
        {
          title: '职务',
          dataIndex: 'duty',
          width: 80,
        },
      ],
      selectedRowKeys: [],
      selectedRows: [],
      btLoading: false,
      searchParams: {},
    };
  }

  //获取列表
  getList = () => {
    const { searchParams } = this.state;

    this.table.search(searchParams);
  };

  //搜索
  search = values => {
    this.setState({ searchParams: { ...values } }, () => {
      this.getList();
    });
  };

  //清除
  clear = () => {
    this.setState({ searchParams: {} }, () => {
      this.getList();
    });
  };

  // 选择
  onRowSelectChange = (selectedRowKeys, selectedRows) => {
    this.setState({ selectedRowKeys, selectedRows });
    this.props.form.setFieldsValue({ userOids: selectedRowKeys });
  };
  onClose = () => {
    this.props.onClose();
  };
  // 关闭标签
  closeTag = userOid => {
    let { selectedRowKeys, selectedRows } = this.state;
    selectedRowKeys.splice(selectedRowKeys.findIndex(item => item === userOid), 1);
    selectedRows.splice(selectedRows.findIndex(item => item.userOid === userOid), 1);
    this.setState({ selectedRowKeys, selectedRows });
    this.props.form.setFieldsValue({ userOids: selectedRowKeys });
  };
  // 清空选择的
  clearSelected = () => {
    this.setState({ selectedRowKeys: [], selectedRows: [] });
    this.props.form.setFieldsValue({ userOids: [] });
  };
  // 确定
  okHandler = () => {
    const { selectedRowKeys } = this.state;
    if (selectedRowKeys.length === 0) {
      message.error('请选择通知员工！');
      return;
    }
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { documentType, documentOid } = this.props;
        this.setState({ btLoading: true });
        let params = { ...values, entityOid: documentOid, entityType: documentType };
        let url = `${config.workflowUrl}/api/workflow/notify`;
        httpFetch
          .post(url, params)
          .then(res => {
            this.setState({ btLoading: false });
            if (res.data.failNum === null || res.data.failNum === 0) {
              message.success(messages('common.operate.success')); //操作成功
              // 返回
              this.onClose();
            } else {
              message.success('操作失败：' + JSON.stringify(res.data.failReason));
              this.setState({ passLoading: false });
            }
          })
          .catch(e => {
            if (e.response) {
              message.error(
                messages('common.operate.filed' /*操作失败*/) + '!' + e.response.data.message
              );
            } else {
              message.error(messages('common.operate.filed'));
            }
            this.setState({ btLoading: false });
          });
      }
    });
  };
  render() {
    const { btLoading, selectedRowKeys, columns, searchForm, selectedRows } = this.state;
    const rowSelection = {
      onChange: this.onRowSelectChange,
      selectedRowKeys: selectedRowKeys,
    };
    const pagination = { pageSize: 5 };
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        visible={this.props.modalVisible}
        width={800}
        confirmLoading={btLoading}
        destroyOnClose={true}
        title="通知"
        onCancel={this.onClose}
        footer={[
          <div key="footerKey">
            <span key="clearKey" style={{ float: 'left' }}>
              已选 {selectedRowKeys.length}条 &nbsp;&nbsp;&nbsp;&nbsp;{' '}
              <a onClick={this.clearSelected}>清空</a>
            </span>,
            <Button key="back" onClick={this.onClose} loading={btLoading}>
              取消
            </Button>,
            <Button key="confirmKey" loading={btLoading} type="primary" onClick={this.okHandler}>
              确定
            </Button>
          </div>,
        ]}
      >
        <div className="table-header">
          <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
          <div style={{ margin: '10px 0' }}>已选择{selectedRowKeys.length}条数据</div>
          <CustomTable
            tableKey="userOid"
            rowSelection={rowSelection}
            ref={ref => (this.table = ref)}
            columns={columns}
            tableSize="small"
            params={{ status: '1001' }}
            pagination={pagination}
            url={`${config.mdataUrl}/api/users/v3/search`}
          />
          <Form>
            <Row>
              <Col span={24}>
                <FormItem label={'通知员工'} labelCol={{ span: 3 }} wrapperCol={{ span: 20 }}>
                  {getFieldDecorator('userOids', {
                    rules: [
                      {
                        required: true,
                      },
                    ],
                  })(
                    <div>
                      {selectedRowKeys.length > 0 &&
                        selectedRowKeys.map(userOid => {
                          let record = selectedRows.find(item => userOid === item.userOid);
                          return (
                            <Tag key={userOid} closable onClose={() => this.closeTag(userOid)}>
                              {record.employeeId + '-' + record.fullName}
                            </Tag>
                          );
                        })}
                    </div>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={24}>
                <FormItem label={'通知内容'} labelCol={{ span: 3 }} wrapperCol={{ span: 20 }}>
                  {getFieldDecorator('remark', {
                    rules: [
                      {
                        required: true,
                        message: this.$t({ id: 'common.please.input' } /*请输入*/),
                      },
                    ],
                  })(<TextArea autosize={{ minRows: 2 }} />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </div>
      </Modal>
    );
  }
}
Notify.propTypes = {
  modalVisible: PropTypes.bool, // 是否展示
};

Notify.defaultProps = {
  modalVisible: false,
};

const wrappedNotify = Form.create(mapStateToProps)(Notify);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedNotify);
