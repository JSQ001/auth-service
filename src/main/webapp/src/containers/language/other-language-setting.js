import React, { Component } from 'react';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Button, Input, message, Popover, Divider, Modal, Tabs } from 'antd';
import Table from 'widget/table';
import SearchArea from 'widget/search-area';
import service from './service';
import httpFetch from '../../utils/fetch';
import SettingServer from './other-language-setting-serve';

class LanguageManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      addShow: false,
      selectedRowKeys: [],
      columns: [
        {
          title: 'key',
          dataIndex: 'keyCode',
          width: 180,
          render: value => <Popover content={value}>{value}</Popover>,
        },
        {
          title: '中文',
          dataIndex: 'sourceKeyDescription',
        },
        {
          title: '描述',
          dataIndex: 'targetKeyDescription',
          render: (value, record, index) =>
            record.isEdit ? (
              <Input
                value={value}
                onChange={e => {
                  this.onChange(e, index);
                }}
              />
            ) : (
              value
            ),
        },
        {
          title: '操作',
          dataIndex: 'option',
          width: 120,
          align: 'center',
          render: (value, record, index) => {
            return !record.isEdit ? (
              <span>
                <a onClick={() => this.oneEdit(record, index)}>编辑</a>
                <Divider type="vertical" />
                <a onClick={() => this.oneTrans(record, index)}>翻译</a>
              </span>
            ) : (
              <span>
                <a onClick={() => this.oneSave(record, index)}>保存</a>
                <Divider type="vertical" />
                <a onClick={() => this.oneCancel(record, index)}>取消</a>
              </span>
            );
          },
        },
      ],
      pagination: {
        total: 0,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 10,
        current: 1,
      },
      searchParams: {},
      loading: true,
      translateLoading: false,
      saveLoading: false,
      cacheDara: {},
      searchForm: [
        {
          type: 'input',
          label: 'key',
          id: 'keyCode',
          colSpan: 6,
        },
      ],
      activeKey: 'web',
    };
  }

  componentDidMount() {
    this.getList();
  }

  handleTabChange = key => {
    this.setState({ activeKey: key });
  };

  onChange = (e, index) => {
    const { dataSource } = this.state;
    dataSource[index].targetKeyDescription = e.target.value;
    this.setState({ dataSource });
  };

  add = () => {
    this.setState({ addShow: true });
  };

  handleSubmit = e => {
    e.preventDefault();

    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        values = {
          ...values,
          moduleId: this.props.match.params.moduleId,
          lang: this.props.match.params.langType,
        };
        service.addLanguage(values).then(res => {
          message.success('保存成功！');
          this.setState({ addShow: false });
          this.props.form.resetFields();
        });
      }
    });
  };

  getList = () => {
    const { appId, langType } = this.props.match.params;
    const {
      pagination,
      pagination: { current, pageSize },
      searchParams,
    } = this.state;
    this.setState({ loading: true, cacheDara: {} });
    service
      .getOtherMessages({
        applicationId: appId,
        sourceLanguage: 'zh_cn',
        targetLanguage: langType,
        page: current - 1,
        size: pageSize,
        ...searchParams,
      })
      .then(res => {
        pagination.total = Number(res.headers['x-total-count']) || 0;
        this.setState({ dataSource: res.data, loading: false, pagination });
      })
      .catch(err => {
        message.error(err.response.data.messages);
      });
  };

  oneSave = (record, index) => {
    const { dataSource, cacheDara } = this.state;
    const { langType, appId, appCode } = this.props.match.params;
    if (record.targetId) {
      service
        .updateBatchFrontKey([
          {
            id: record.targetId,
            applicationId: appId,
            applicationCode: appCode,
            keyCode: record.keyCode,
            keyDescription: record.targetKeyDescription,
            language: langType,
            deleted: false,
            versionNumber: 1,
          },
        ])
        .then(res => {
          res.data.forEach(item => {
            const data = dataSource.find(o => o.keyCode === item.keyCode);
            data.isEdit = false;
            data.targetId = item.id;
            data.versionNumber = item.versionNumber;
            delete cacheDara[data.keyCode];
          });
          message.success('操作成功！');
          this.setState({ dataSource });
        })
        .catch(err => {
          console.log(err);
          message.error('操作失败！');
        });
    } else {
      service
        .addBatchFrontKey([
          {
            applicationId: appId,
            language: langType,
            applicationCode: appCode,
            keyCode: record.keyCode,
            keyDescription: record.targetKeyDescription,
          },
        ])
        .then(res => {
          res.data.forEach(item => {
            const data = dataSource.find(o => o.keyCode === item.keyCode);
            data.isEdit = false;
            data.targetId = item.id;
            data.versionNumber = item.versionNumber;
            delete cacheDara[data.keyCode];
          });
          message.success('操作成功！');
          this.setState({ dataSource });
        })
        .catch(err => {
          message.error('操作失败！');
        });
    }
  };

  save = () => {
    const adds = [];
    const updates = [];
    const { langType, appId, appCode } = this.props.match.params;
    const { selectedRowKeys, dataSource, cacheDara } = this.state;

    const rows = dataSource.filter(o => selectedRowKeys.includes(o.keyCode));

    rows.map(item => {
      if (!item.isEdit) return;
      if (item.targetId) {
        updates.push({
          id: item.targetId,
          applicationId: appId,
          applicationCode: appCode,
          keyCode: item.keyCode,
          keyDescription: item.targetKeyDescription,
          language: langType,
          deleted: false,
          versionNumber: 1,
        });
      } else {
        adds.push({
          applicationId: appId,
          language: langType,
          applicationCode: appCode,
          keyCode: item.keyCode,
          keyDescription: item.targetKeyDescription,
        });
      }
    });

    if (!adds.length && !updates.length) return;
    this.setState({ saveLoading: true });
    Promise.all([service.addBatchFrontKey(adds), service.updateBatchFrontKey(updates)])
      .then(res => {
        const list = res.reduce((temp, item) => {
          temp.push(...item.data);
          return temp;
        }, []);
        list.forEach(item => {
          const data = dataSource.find(o => o.keyCode === item.keyCode);
          data.isEdit = false;
          data.targetId = item.id;
          data.versionNumber = item.versionNumber;
          delete cacheDara[data.keyCode];
        });
        message.success('操作成功！');
        this.setState({ dataSource, saveLoading: false });
      })
      .catch(err => {
        message.error('操作失败！');
        this.setState({ saveLoading: false });
      });
  };

  handleCancel = () => {
    this.setState({ addShow: false });
  };

  // 单个编辑
  oneEdit = (record, index) => {
    const { cacheDara, dataSource } = this.state;
    if (!(record.keyCode in cacheDara)) {
      cacheDara[record.keyCode] = { ...record };
    }
    dataSource[index].isEdit = true;
    this.setState({ cacheDara, dataSource });
  };

  oneCancel = (record, index) => {
    const { cacheDara, dataSource } = this.state;
    dataSource[index] = { ...cacheDara[record.keyCode] };
    delete cacheDara[record.keyCode];
    this.setState({ dataSource, cacheDara });
  };

  oneTrans = (record, index) => {
    const { dataSource, cacheDara } = this.state;
    const { langType } = this.props.match.params;

    cacheDara[record.keyCode] = { ...record };

    const params = {
      languages: [record.sourceKeyDescription],
      to: langType,
    };

    httpFetch.post('/api/transfer', params).then(result => {
      result.map(item => {
        let words = String(item.dst).split(' ');

        words = words.map(item => {
          return item[0].toUpperCase() + item.substring(1, item.length);
        });

        dataSource[index].targetKeyDescription = words.join(' ');
        dataSource[index].isEdit = true;
      });
      this.setState({ dataSource, cacheDara });
    });
  };

  trans = () => {
    const { langType } = this.props.match.params;
    const { selectedRowKeys, dataSource, cacheDara } = this.state;

    const rows = dataSource.filter(o => selectedRowKeys.includes(o.keyCode));

    const params = {
      languages: rows.map(item => item.sourceKeyDescription),
      to: langType,
    };

    rows.forEach(item => {
      if (!(item.keyCode in cacheDara)) {
        cacheDara[item.keyCode] = { ...item };
      }
    });

    this.setState({ translateLoading: true, loading: true, cacheDara });
    httpFetch.post('/api/transfer', params).then(result => {
      result.map((item, index) => {
        let words = String(item.dst).split(' ');

        words = words.map(item => {
          return item[0].toUpperCase() + item.substring(1, item.length);
        });

        rows[index].targetKeyDescription = words.join(' ');
        rows[index].isEdit = true;
      });
      this.setState({ dataSource, translateLoading: false, loading: false });
    });
  };

  back = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/setting/language/language-modules/${this.props.match.params.langType}`,
      })
    );
  };

  cancel = () => {
    const { dataSource, cacheDara, selectedRowKeys } = this.state;

    const rows = dataSource.filter(o => selectedRowKeys.includes(o.keyCode));

    rows.forEach(item => {
      if (item.keyCode in cacheDara) {
        item.isEdit = false;
        item.targetKeyDescription = cacheDara[item.keyCode].targetKeyDescription;
        delete cacheDara[item.keyCode];
      }
    });
    this.setState({ dataSource });
  };

  handleTableChange = pagination => {
    const { dataSource } = this.state;
    if (dataSource.some(o => o.isEdit)) {
      Modal.confirm({
        title: '警告',
        content: '当前页面存在未保存的信息，跳转后信息会丢失，是否继续跳转？',
        onOk: () => {
          this.setState({ pagination }, () => {
            this.getList();
          });
        },
      });
    } else {
      this.setState({ pagination }, () => {
        this.getList();
      });
    }
  };

  search = values => {
    const { dataSource } = this.state;
    if (dataSource.some(o => o.isEdit)) {
      Modal.confirm({
        title: '警告',
        content: '当前页面存在未保存的信息，搜索后信息会丢失，是否继续搜索？',
        onOk: () => {
          this.setState({ searchParams: values }, this.getList);
        },
      });
    } else {
      this.setState({ searchParams: values }, this.getList);
    }
  };

  onSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  render() {
    const {
      columns,
      dataSource,
      selectedRowKeys,
      pagination,
      loading,
      translateLoading,
      saveLoading,
      searchForm,
      activeKey,
    } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    return (
      <div style={{ backgroundColor: '#fff', padding: 10, overflow: 'auto' }}>
        <Tabs defaultActiveKey={activeKey} onChange={this.handleTabChange}>
          <Tabs.TabPane tab="客户端国际化信息" key="web">
            <div style={{ paddingTop: '10px' }}>
              <SearchArea
                searchForm={searchForm}
                maxLength={4}
                clearHandle={() => {}}
                submitHandle={this.search}
              />
              <div style={{ margin: '10px 0' }}>
                <Button
                  loading={translateLoading}
                  disabled={!selectedRowKeys.length}
                  onClick={this.trans}
                  type="primary"
                >
                  自动翻译
                </Button>
                <Button
                  disabled={!selectedRowKeys.length}
                  loading={saveLoading}
                  style={{ marginLeft: 20 }}
                  onClick={this.save}
                  type="primary"
                >
                  全部保存
                </Button>
                <Button
                  disabled={!selectedRowKeys.length}
                  style={{ marginLeft: 20 }}
                  onClick={this.cancel}
                >
                  全部取消
                </Button>
                <Button style={{ marginLeft: 20 }} onClick={this.back}>
                  返回到模块列表
                </Button>
              </div>
              <Table
                size="middle"
                rowKey="keyCode"
                bordered
                dataSource={dataSource}
                columns={columns}
                pagination={pagination}
                rowSelection={rowSelection}
                onChange={this.handleTableChange}
                loading={loading}
              />
            </div>
          </Tabs.TabPane>
          <Tabs.TabPane tab="服务端国际化信息" key="serve">
            <SettingServer params={this.props.match.params} />
          </Tabs.TabPane>
        </Tabs>
      </div>
    );
  }
}

export default connect()(LanguageManager);
