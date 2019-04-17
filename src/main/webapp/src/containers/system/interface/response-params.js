import React from 'react';
import { connect } from 'dva';
import { Button, Input, Divider, message, Select, Tooltip } from 'antd';
import Table from 'widget/table';
import service from './interface.service';
import debounce from 'lodash/debounce';
import baseCommon from '../../../services/common';
import RequestList from './requeet-list';

class ResponseParams extends React.Component {
  constructor(props) {
    super(props);
    this.handleSearch = debounce(this.handleSearch, 300);
    this.state = {
      record: null,
      dataSource: [],
      isEdit: false,
      loading: false,
      id: 1,
      expandedRowKeys: [],
      messages: [],
      showMessages: [],
      visible: false,
      columns: [
        {
          title: '字段',
          align: 'left',
          dataIndex: 'keyCode',
          width: 200,
          render: (value, record, index) => {
            return record.status == 'edit' || record.status == 'new' ? (
              <Input
                style={{ width: 128 - (record.level - 1) * 14 }}
                onChange={e => this.change('keyCode', e.target.value, record)}
                value={value}
              />
            ) : (
              <span>{value}</span>
            );
          },
        },
        {
          title: '类型',
          dataIndex: 'respType',
          width: 120,
          render: (value, record, index) => {
            return record.status == 'edit' || record.status == 'new' ? (
              <Select
                style={{ width: '100%' }}
                onChange={e => this.change('respType', e, record)}
                value={value}
              >
                <Select.Option value="string">string</Select.Option>
                <Select.Option value="array">array</Select.Option>
                <Select.Option value="bool">bool</Select.Option>
                <Select.Option value="date">date</Select.Option>
                <Select.Option value="int">int</Select.Option>
                <Select.Option value="long">long</Select.Option>
                <Select.Option value="float">float</Select.Option>
                <Select.Option value="double">double</Select.Option>
                <Select.Option value="decimal">decimal</Select.Option>
              </Select>
            ) : (
              <span>{value}</span>
            );
          },
        },
        {
          title: '名称',
          dataIndex: 'name',
          render: (value, record) => {
            return record.status == 'edit' || record.status == 'new' ? (
              <Select
                showSearch
                value={value}
                placeholder="请输入"
                defaultActiveFirstOption={false}
                showArrow={false}
                filterOption={false}
                onSearch={this.handleSearch}
                onChange={value => this.change('name', value, record)}
                notFoundContent={null}
                style={{ maxWidth: 300, width: '100%' }}
              >
                {this.state.showMessages.map(o => (
                  <Select.Option key={o.key}>
                    <Tooltip title={`${o.key}-${o.label}`}>
                      {o.key}-{o.label}
                    </Tooltip>
                  </Select.Option>
                ))}
              </Select>
            ) : (
              <span>{value}</span>
            );
          },
        },
        {
          title: '操作',
          dataIndex: 'option',
          width: 160,
          align: 'center',
          render: (value, record, index) => {
            return record.status == 'edit' || record.status == 'new' ? (
              <span>
                <a onClick={() => this.save(record)}>保存</a>
                <Divider type="vertical" />
                <a onClick={() => this.cancel(record)}>取消</a>
              </span>
            ) : (
              <span>
                {record.type == 'object' && (
                  <a onClick={() => this.addChildren(record, index)}>添加</a>
                )}
                {record.type == 'object' && <Divider type="vertical" />}
                <a onClick={() => this.edit(record)}>编辑</a>
                <Divider type="vertical" />
                <a onClick={() => this.delete(record, index)}>删除</a>
              </span>
            );
          },
        },
      ],
    };
  }

  componentDidMount() {
    service.getResponseList(this.props.id).then(res => {
      res.map(item => {
        item.status = 'normal';
        item.level = 1;
      });
      this.setState({ dataSource: res });
    });
  }

  handleSearch = value => {
    let { messages } = this.state;
    if (messages.length === 0) {
      messages = Object.keys(this.props.languages.languages).map(key => {
        return {
          key,
          label: this.props.languages.languages[key],
        };
      });
    }
    const showMessages = messages.filter(o => o.label === value || o.key === value).slice(0, 20);
    showMessages.push(
      ...messages
        .filter(
          o =>
            (o.label.indexOf(value) >= 0 || o.key.indexOf(value) >= 0) &&
            (o.label !== value && o.key !== value)
        )
        .slice(0, 20 - showMessages.length)
    );
    this.setState({ showMessages, messages });
  };

  change = (key, value, { id }) => {
    let dataSource = this.state.dataSource;
    let record = this.getDataById(dataSource, id);
    record[key] = value;
    this.setState({ dataSource });
  };

  save = ({ id }) => {
    let dataSource = this.state.dataSource;
    let record = this.getDataById(dataSource, id);
    let params = { ...record, interfaceId: this.props.id };
    if (!params.id || params.id == '0') {
      delete params.id;
      service
        .addResponse(params)
        .then(res => {
          record.id = res.id;
          record.status = 'normal';
          this.setState({ dataSource, isEdit: false });
        })
        .catch(err => {
          message.error('保存失败！');
        });
    } else {
      service
        .updateResponse(params)
        .then(res => {
          record.id = res.id;
          record.status = 'normal';
          record.versionNumber = res.versionNumber;
          this.setState({ dataSource, isEdit: false });
        })
        .catch(err => {
          message.error('保存失败！');
        });
    }
  };

  cancel = record => {
    let dataSource = this.state.dataSource;
    let parent = this.getDataById(dataSource, record.parentId);
    let status = record.status;

    if (status == 'new') {
      if (parent) {
        parent.children.splice(parent.children.findIndex(o => o.id == record.id), 1);
      } else {
        dataSource.splice(dataSource.findIndex(o => o.id == record.id), 1);
      }
    } else if (status == 'edit') {
      if (parent) {
        parent.children[parent.children.findIndex(o => o.id == record.id)] = this.state.record;
      } else {
        dataSource[dataSource.findIndex(o => o.id == record.id)] = this.state.record;
      }
    }

    this.setState({ dataSource, record: null, isEdit: false });
  };

  edit = ({ id }) => {
    if (this.state.isEdit) {
      message.warning('有未保存的数据，请先保存！');
      return;
    }

    let dataSource = this.state.dataSource;
    let data = this.getDataById(dataSource, id);

    let record = { ...data };
    data.status = 'edit';
    this.setState({ dataSource, record, isEdit: true });
  };

  add = () => {
    if (this.state.isEdit) {
      message.warning('有未保存的数据，请先保存！');
      return;
    }

    let dataSource = this.state.dataSource;
    let id = this.state.id;

    dataSource.push({
      id: '0',
      status: 'new',
      level: 1,
      parentId: 0,
    });
    this.setState({ dataSource, isEdit: true, id: id + 1 });
  };

  delete = (record, index) => {
    service.deleteResponse(record.id).then(res => {
      message.success('删除成功！');
      let dataSource = this.state.dataSource;
      dataSource.splice(index, 1);
      this.setState({ dataSource });
    });
  };

  expand = expandedRowKeys => {
    this.setState({ expandedRowKeys });
  };

  addChildren = data => {
    if (this.state.isEdit) {
      message.warning('有未保存的数据，请先保存！');
      return;
    }

    let expandedRowKeys = this.state.expandedRowKeys;
    let dataSource = this.state.dataSource;
    let record = this.getDataById(dataSource, data.id);

    if (record.children) {
      record.children.push({
        id: '0',
        status: 'new',
        level: record.level + 1,
        parentId: record.id,
      });
    } else {
      record.children = [
        {
          id: '0',
          status: 'new',
          level: record.level + 1,
          parentId: record.id,
        },
      ];
    }
    expandedRowKeys.push(record.id);
    this.setState({ dataSource, isEdit: true, expandedRowKeys });
  };

  getLastKey = pos => {
    if (!pos) return 0;
    return pos.split('-')[pos.split('-').length - 1];
  };

  getParent = (dataSource, pos) => {
    if (String(pos).includes('-')) {
      let posArray = pos.split('-');
      posArray.splice(posArray.length - 1, 1);
      return this.getDataByPos(dataSource, posArray.join('-'));
    } else {
      return null;
    }
  };

  getDataById = (dataSource, id) => {
    //let data = dataSource.find(o=> o.id == id);
    if (!id) return null;
    for (let i = 0; i < dataSource.length; i++) {
      if (dataSource[i].id == id) {
        return dataSource[i];
      } else {
        let data = this.getDataById(dataSource[i].children || [], id);
        if (data) {
          return data;
        }
      }
    }
  };

  autoFillHandle = () => {
    this.setState({ visible: true });
  };

  autoFill = values => {
    const { interfaceInfo } = this.props;
    values = { ...values, page: 0, size: 1 };
    baseCommon
      .getInterfaceData(interfaceInfo, values)
      .then(res => {
        if (!res.data.length) {
          message.error('接口没有返回数据，请检查搜索条件。');
          return;
        }
        this.batchSave(res.data[0]);
      })
      .catch(err => {
        console.log(err);
        message.error(err.response.data.message);
      });
  };

  batchSave = (data = {}) => {
    let result = [];
    Object.keys(data).map(key => {
      result.push({ keyCode: key, interfaceId: this.props.id });
    });

    service.addResponses(result).then(dataSource => {
      dataSource.map(item => {
        item.status = 'normal';
        item.level = 1;
      });
      this.setState({ dataSource, loading: false, visible: false });
    });
  };

  render() {
    const { dataSource, columns, expandedRowKeys, loading, visible } = this.state;
    const { id } = this.props;
    return (
      <div className="request-params">
        <div style={{ marginTop: 0, marginBottom: 20 }} className="table-header">
          {!!dataSource.length && (
            <Button style={{ marginRight: 10 }} onClick={this.autoFillHandle} type="primary">
              自动填充
            </Button>
          )}
          <Button onClick={this.add} type="primary">
            添加
          </Button>
        </div>
        <Table
          indentSize={14}
          rowKey="id"
          size="small"
          bordered
          dataSource={dataSource}
          columns={columns}
          expandedRowKeys={expandedRowKeys}
          onExpandedRowsChange={this.expand}
          scroll={{ y: 320, x: 800 }}
          pagination={false}
          loading={loading}
        />
        <RequestList
          id={id}
          onOk={this.autoFill}
          onCancel={() => {
            this.setState({ visible: false });
          }}
          visible={visible}
        />
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    languages: state.languages,
  };
}
export default connect(mapStateToProps)(ResponseParams);
