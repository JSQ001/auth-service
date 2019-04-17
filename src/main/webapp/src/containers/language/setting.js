import React, { Component } from 'react';
import { Button, Modal, Form, Input, message, Tabs, Popconfirm, Select } from 'antd';
import service from './service';
import CustomTable from 'widget/custom-table';
import SearchArea from 'widget/search-area';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import config from 'config';

class LanguageManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modules: [],
      addShow: false,
      record: {},
      searchForm: [
        {
          type: 'input',
          id: 'keyCode',
          label: 'key',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'keyDescription',
          label: this.$t('chooser.data.description'),
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: 'key',
          dataIndex: 'keyCode',
        },
        {
          title: '描述',
          dataIndex: 'keyDescription',
        },
        {
          title: '操作',
          dataIndex: 'id',
          width: 100,
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.edit(record)}>编辑</a>
                <span className="ant-divider" />
                <Popconfirm onConfirm={() => this.delete(value)} title="确定删除？">
                  <a>删除</a>
                </Popconfirm>
              </span>
            );
          },
        },
      ],
      serveColumns: [
        {
          title: 'key',
          dataIndex: 'keyCode',
        },
        {
          title: '描述',
          dataIndex: 'keyDescription',
        },
        {
          title: '消息类型',
          dataIndex: 'categoryName',
          width: 120,
        },
        {
          title: '操作',
          dataIndex: 'id',
          width: 100,
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.edit(record)}>编辑</a>
                <span className="ant-divider" />
                <Popconfirm onConfirm={() => this.delete(value)} title="确定删除？">
                  <a>删除</a>
                </Popconfirm>
              </span>
            );
          },
        },
      ],
      typeList: [],
      activeKey: 'web',
    };
  }

  componentDidMount() {
    this.getSystemValueList('CATEGORY')
      .then(res => {
        this.setState({ typeList: res.data.values });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  add = () => {
    this.setState({ addShow: true, record: {} });
  };

  edit = value => {
    this.props.form.setFieldsValue({
      keyCode: value.keyCode,
      keyDescription: value.keyDescription,
      category: value.category,
    });
    this.setState({ addShow: true, record: value });
  };

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const {
          record: { id },
          record,
        } = this.state;
        const { appId, appCode } = this.props.match.params;
        values = {
          ...record,
          ...values,
          applicationId: appId,
          applicationCode: appCode,
          language: 'zh_cn',
        };
        //前端多语言提交
        if (this.state.activeKey === 'web') {
          this.webSubmit(!id, values);
        } else {
          this.serveSubmit(!id, values);
        }
      }
    });
  };

  // 前端多语言提交
  webSubmit = (isNew, values) => {
    if (isNew) {
      service
        .addWebLocale(values)
        .then(() => {
          message.success('保存成功！');
          this.setState({ addShow: false });
          this.props.form.resetFields();
          this.webTable.reload();
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    } else {
      service
        .updateWebLocale(values)
        .then(() => {
          message.success('保存成功！');
          this.setState({ addShow: false });
          this.props.form.resetFields();
          this.webTable.reload();
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  // 后端多语言提交
  serveSubmit = (isNew, values) => {
    if (isNew) {
      service
        .addServeLocale(values)
        .then(() => {
          message.success('保存成功！');
          this.setState({ addShow: false });
          this.props.form.resetFields();
          this.serveTable.reload();
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    } else {
      service
        .updateServeLocale(values)
        .then(() => {
          message.success('保存成功！');
          this.setState({ addShow: false });
          this.props.form.resetFields();
          this.serveTable.reload();
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  handleCancel = () => {
    this.setState({ addShow: false });
    this.props.form.resetFields();
  };

  back = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/setting/language/language-modules/zh_cn',
      })
    );
  };

  delete = id => {
    if (this.state.activeKey === 'web') {
      service.deleteWebLocale(id).then(res => {
        message.success('删除成功！');
        this.webTable.reload();
      });
    } else {
      service.deleteServeLocale(id).then(res => {
        message.success('删除成功！');
        this.serveTable.reload();
      });
    }
  };

  webSearch = values => {
    this.webTable.search(values);
  };

  serveSearch = values => {
    this.serveTable.search(values);
  };

  handleTabChange = key => {
    this.setState({ activeKey: key });
  };

  render() {
    const { columns, addShow, record, searchForm, typeList, serveColumns, activeKey } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { appId } = this.props.match.params;

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };
    return (
      <div style={{ backgroundColor: '#fff', padding: 10, overflow: 'auto' }}>
        <Tabs defaultActiveKey={activeKey} onChange={this.handleTabChange}>
          <Tabs.TabPane tab="客户端国际化信息" key="web">
            <SearchArea
              searchForm={searchForm}
              maxLength={4}
              clearHandle={() => {
                this.webTable.reload();
              }}
              submitHandle={this.webSearch}
            />
            <Button style={{ margin: '10px 0' }} onClick={this.add} type="primary">
              添加
            </Button>
            <Button style={{ margin: '10px 0', marginLeft: 20 }} onClick={this.back}>
              返回到应用列表
            </Button>
            <CustomTable
              ref={ref => (this.webTable = ref)}
              url={`${config.baseUrl}/api/front/locale/query/by/cond`}
              columns={columns}
              params={{ lang: 'zh_cn', applicationId: appId }}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab="服务端国际化信息" key="serve">
            <SearchArea
              searchForm={searchForm}
              maxLength={4}
              clearHandle={() => {
                this.serveTable.reload();
              }}
              submitHandle={this.serveSearch}
            />
            <Button style={{ margin: '10px 0' }} onClick={this.add} type="primary">
              添加
            </Button>
            <Button style={{ margin: '10px 0', marginLeft: 20 }} onClick={this.back}>
              返回到应用列表
            </Button>
            <CustomTable
              ref={ref => (this.serveTable = ref)}
              url={`${config.baseUrl}/api/serve/locale/query/by/cond`}
              columns={serveColumns}
              params={{ lang: 'zh_cn', applicationId: appId }}
            />
          </Tabs.TabPane>
        </Tabs>
        <Modal
          title="添加/编辑语言"
          visible={addShow}
          onOk={this.handleSubmit}
          onCancel={this.handleCancel}
        >
          <Form>
            <Form.Item {...formItemLayout} label="key">
              {getFieldDecorator('keyCode')(<Input disabled={!!record.id} placeholder="请输入" />)}
            </Form.Item>
            <Form.Item {...formItemLayout} label="描述">
              {getFieldDecorator('keyDescription')(<Input placeholder="请输入" />)}
            </Form.Item>
            {activeKey === 'serve' && (
              <Form.Item {...formItemLayout} label="信息类型">
                {getFieldDecorator('category')(
                  <Select placeholder="请选择">
                    {typeList.map(item => {
                      return <Select.Option key={item.value}>{item.name}</Select.Option>;
                    })}
                  </Select>
                )}
              </Form.Item>
            )}
          </Form>
        </Modal>
      </div>
    );
  }
}

export default connect()(Form.create()(LanguageManager));
