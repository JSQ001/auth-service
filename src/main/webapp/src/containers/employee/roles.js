import React from 'react';
import { Modal, message, Button, Popconfirm, Select, Divider, DatePicker } from 'antd';
import service from './service';
import SearchArea from '../../components/Widget/search-area';
import Table from 'widget/table';
import moment from 'moment';
import { Promise } from 'es6-promise';

const Option = Select.Option;
const confirm = Modal.confirm;

class SelectRoles extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      roleName: [],
      dataAuthority: [],
      columns: [
        {
          title: this.$t('mdata.character.name') /*角色名称*/,
          dataIndex: 'roleName',
          align: 'center',
          className: 'required',
          render: (value, record, index) => {
            if (record.status) {
              return (
                <Select
                  defaultValue={record.roleId ? { key: record.roleId } : undefined}
                  style={{ width: '90%' }}
                  onChange={value => this.rolChange(value, index)}
                  labelInValue
                  placeholder={this.$t('mdata.please.select.a')} /*请选择*/
                  dropdownMatchSelectWidth={false}
                  getPopupContainer={node => node.parentNode}
                >
                  {this.state.roleName.map(item => {
                    return <Option key={item.id}>{item.roleName}</Option>;
                  })}
                </Select>
              );
            } else {
              return value;
            }
          },
        },
        {
          title: this.$t('mdata.data.access.name') /*数据权限名称*/,
          dataIndex: 'dataAuthorityName',
          className: 'required',
          render: (value, record, index) => {
            if (record.status) {
              return (
                <Select
                  defaultValue={
                    record.dataAuthorityId ? { key: record.dataAuthorityId } : undefined
                  }
                  style={{ width: '90%' }}
                  onChange={value => this.dataAuthorityChange(value, index)}
                  labelInValue
                  placeholder={this.$t('mdata.please.select.a')} /*请选择*/
                  dropdownMatchSelectWidth={false}
                  getPopupContainer={node => node.parentNode}
                >
                  {this.state.dataAuthority.map(item => {
                    return <Option key={item.id}>{item.dataAuthorityName}</Option>;
                  })}
                </Select>
              );
            } else {
              return value;
            }
          },
        },
        {
          title: this.$t('mdata.the.effective.date') /*有效日期*/,
          dataIndex: 'validDateFrom',
          width: '260px',
          className: 'required',
          render: (value, record, index) => {
            let validDateTo = record.validDateTo;
            if (record.status) {
              return (
                <>
                  <DatePicker
                    defaultValue={value ? moment(value) : null}
                    onChange={date => this.timeBeginChange(date, index)}
                    placeholder={this.$t('mdata.effective.date.from')} /*有效日期从*/
                    style={{ width: '48%', float: 'left' }}
                    getCalendarContainer={node => node.parentNode}
                  />
                  <DatePicker
                    defaultValue={validDateTo ? moment(validDateTo) : null}
                    onChange={date => this.timeEndChange(date, index)}
                    placeholder={this.$t('mdata.effective.date.to')} /*有效日期至*/
                    style={{ width: '48%', float: 'right' }}
                    getCalendarContainer={node => node.parentNode}
                  />
                </>
              );
            } else {
              if (value) {
                return (
                  (value ? moment(value).format('YYYY-MM-DD') : '') +
                  ' ~ ' +
                  (validDateTo ? moment(validDateTo).format('YYYY-MM-DD') : '')
                );
              }
            }
          },
        },
        {
          title: this.$t('mdata.operation') /*操作*/,
          dataIndex: 'operation',
          align: 'center',
          width: '120px',
          render: (value, record, index) => {
            if (record.status) {
              return (
                <span>
                  <a onClick={() => this.saveRow(index)}>{this.$t('mdata.save')}</a>
                  {/*保存*/}
                  <Divider type="vertical" />
                  <a onClick={() => this.cancel(record, index)}>{this.$t('mdata.cancel')}</a>
                  {/*取消*/}
                </span>
              );
            } else {
              return (
                <span>
                  <a
                    onClick={() => {
                      this.edit(record, index);
                    }}
                  >
                    {this.$t('mdata.the.editor')}
                    {/*编辑*/}
                  </a>
                  <Divider type="vertical" />
                  <Popconfirm
                    title={this.$t('mdata.desc.code4')} // 你确定要删除？
                    onConfirm={() => this.delete(record.id, index)}
                    okText={this.$t('mdata.determine')} /*确定*/
                    cancelText={this.$t('mdata.cancel')} /*取消*/
                  >
                    <a>{this.$t('mdata.delete')}</a>
                    {/*删除*/}
                  </Popconfirm>
                </span>
              );
            }
          },
        },
      ],
      dataSource: [],
      loading: false,
      searchForm: [
        {
          label: this.$t('mdata.character.name') /*角色名称*/,
          type: 'input',
          id: 'roleName',
          colSpan: 6,
        },
        {
          label: this.$t('mdata.data.access.name') /*数据权限名称*/,
          type: 'input',
          id: 'dataAuthorityName',
          colSpan: 6,
        },
        {
          label: this.$t('mdata.effective.date.from') /*有效日期从*/,
          type: 'date',
          id: 'validDateFrom',
          colSpan: 6,
          placeholder: this.$t('mdata.effective.date.from') /*有效日期从*/,
        },
        {
          label: this.$t('mdata.effective.date.to') /*有效日期至*/,
          type: 'date',
          id: 'validDateTo',
          colSpan: 6,
          placeholder: this.$t('mdata.effective.date.to') /*有效日期至*/,
        },
      ],
      dataCache: [],
      pagination: {
        pageSize: 100,
      },
      userId: this.props.userId,
      allSaveLoading: false,
      confirmLoading: false,
      closeFlag: false,
    };
  }

  componentDidMount() {
    this.getList();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.visible && !this.props.visible) {
      //弹框显示
      this.setState({ userId: nextProps.userId, closeFlag: false }, this.getList);
    } else if (!nextProps.visible && this.props.visible) {
      //弹框关闭
      this.searchForm && this.searchForm.handleReset();
    }
  }

  // 获取数据
  getList = values => {
    const { userId } = this.state;
    let params = { userId, ...values };
    this.setState({ loading: true, dataCache: [] });
    this.getSelectData();
    service
      .getRolesDistribute(params)
      .then(res => {
        this.setState({ dataSource: res.data, loading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
      });
  };

  // 角色 数据权限 下拉框的数据
  getSelectData = () => {
    service
      .getAllRoles()
      .then(res => {
        this.setState({ roleName: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    service
      .getDataAuthority()
      .then(res => {
        this.setState({ dataAuthority: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //模态框底部取消
  handleCancel = () => {
    const onCancel = this.props.onCancel;
    if (this.state.dataCache.join('')) {
      confirm({
        title: this.$t('mdata.desc.code5'), // 你还有未保存的修改，确定要关闭？
        onOk() {
          onCancel && onCancel();
        },
        mask: false,
        okText: this.$t('mdata.determine') /*确定*/,
        cancelText: this.$t('mdata.cancel') /*取消*/,
      });
    } else {
      onCancel && onCancel();
    }
  };

  // 模态框底部确定
  handleOk = () => {
    if (this.state.dataCache.join('')) {
      this.setState({ confirmLoading: true, closeFlag: true });
      this.saveAll(); //保存
    } else {
      this.props.onCancel && this.props.onCancel();
    }
  };

  // 搜索
  search = values => {
    let params = {
      ...values,
      validDateFrom: values.validDateFrom && values.validDateFrom.format('YYYY-MM-DD'),
      validDateTo: values.validDateTo && values.validDateTo.format('YYYY-MM-DD'),
    };
    this.getList(params);
  };

  // 编辑
  edit = (record, index) => {
    const { dataSource, dataCache } = this.state;
    record.status = 'edit';
    record.needCache = true;
    dataCache[index] = { ...record };
    this.setState({ dataSource, dataCache });
  };

  //新增一行
  newRow = () => {
    const { dataSource, dataCache, userId } = this.state;
    const empty = { status: 'new', id: new Date().getTime(), userId };
    dataCache[dataSource.length] = { ...empty };
    dataSource.push(empty);
    this.setState({ dataSource, dataCache });
  };

  //编辑时的取消
  cancel = (record, index) => {
    const { dataSource, dataCache } = this.state;
    const flag = record.needCache;
    record.status = false;
    if (flag) {
      dataCache[index] = '';
    } else {
      dataSource.splice(index, 1);
      dataCache.splice(index, 1);
    }
    this.setState({ dataSource, dataCache });
  };

  // 删除
  delete = (id, index) => {
    const { dataCache, dataSource } = this.state;
    service
      .deleteRolesAuthority(id)
      .then(() => {
        dataSource.splice(index, 1);
        dataCache.splice(index, 1);
        this.setState({ dataSource, dataCache }, () => {
          message.success(this.$t('mdata.delete.the.success')); /*删除成功*/
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 保存全部
  saveAll = () => {
    const { dataCache } = this.state;
    if (!this.validateTips(dataCache)) return;
    if (dataCache.join('')) {
      let newData = [],
        editData = [];
      dataCache.forEach(item => {
        switch (item.status) {
          case 'new':
            delete item.id;
            newData.push(item);
            break;
          case 'edit':
            editData.push(item);
            break;
        }
      });
      this.setState({ allSaveLoading: true });
      this.saveAllMethod(newData, editData);
    } else {
      message.warning(this.$t('mdata.desc.code6')); // 没有需要保存的数据
    }
  };

  // 合并请求
  saveAllMethod = (newData, editData) => {
    Promise.all([this.batchSaveRolesAuthority(newData), this.batchUpdateRolesAuthority(editData)])
      .then(() => {
        message.success(this.$t('mdata.save.success')); /*保存成功*/
        this.setState({ allSaveLoading: false, confirmLoading: false }, () => {
          if (this.state.closeFlag) {
            this.props.onCancel && this.props.onCancel();
          } else {
            this.getList();
          }
        });
      })
      .catch(err => {
        if (err.response.data.bizErrorCode === '10031') {
          message.error(this.$t('mdata.desc.code7')); // 该角色与数据权限组合已存在
        } else {
          message.error(err.response.data.message);
        }
        this.setState({ allSaveLoading: false, confirmLoading: false, closeFlag: false });
      });
  };

  // 批量新增
  batchSaveRolesAuthority = newData => {
    if (newData.length) {
      return service.batchSaveRolesAuthority(newData);
    }
  };

  // 批量更新
  batchUpdateRolesAuthority = editData => {
    if (editData.length) {
      return service.batchUpdateRolesAuthority(editData);
    }
  };

  // 保存一行
  saveRow = index => {
    const { dataCache, dataSource } = this.state;
    const data = { ...dataCache[index] };
    let saveMethod;
    if (!this.validateTips(data)) return;
    if (data.status === 'new') {
      saveMethod = service.saveRolesAuthority;
      delete data.id;
    } else if (data.status === 'edit') {
      saveMethod = service.updateRolesAuthority;
    }
    delete data.status;
    saveMethod &&
      saveMethod(data)
        .then(res => {
          dataSource[index] = { ...dataCache[index], ...res.data, status: false };
          dataCache[index] = '';
          this.setState({ dataSource, dataCache });
          message.success(this.$t('mdata.save.success')); /*保存成功*/
        })
        .catch(err => {
          if (err.response.data.bizErrorCode === '10031') {
            message.error(this.$t('mdata.desc.code7')); // 该角色与数据权限组合已存在
          } else {
            message.error(err.response.data.message);
          }
        });
  };

  // 角色下拉框改变 回调
  rolChange = (value, index) => {
    const { dataCache } = this.state;
    dataCache[index].roleId = value.key;
    dataCache[index].roleName = value.label.split('-')[1];
  };

  // 数据权限改变 回调
  dataAuthorityChange = (value, index) => {
    const { dataCache } = this.state;
    dataCache[index].dataAuthorityId = value.key;
    dataCache[index].dataAuthorityName = value.label.split('-')[1];
  };

  //选择日期从 的回调
  timeBeginChange = (date, index) => {
    const { dataCache } = this.state;
    dataCache[index].validDateFrom = date && date.format();
  };

  //选择日期至 的回调
  timeEndChange = (date, index) => {
    const { dataCache } = this.state;
    dataCache[index].validDateTo = date && date.format();
  };

  // 验证提示
  validateTips = data => {
    if (this.validateData(data)) {
      return true;
    } else {
      this.setState({ confirmLoading: false });
      message.warning(this.$t('mdata.desc.code8')); // 注意：角色、数据权限、有效日期从为必填项！
      return false;
    }
  };

  //验证
  validateData = data => {
    if (data instanceof Array) {
      return data.every(item => {
        return item ? item.roleId && item.dataAuthorityId && item.validDateFrom : true;
      });
    } else {
      return data.roleId && data.dataAuthorityId && data.validDateFrom;
    }
  };

  render() {
    const {
      searchForm,
      dataSource,
      dataCache,
      columns,
      loading,
      pagination,
      allSaveLoading,
      confirmLoading,
    } = this.state;
    const { visible } = this.props;
    return (
      <Modal
        title={this.$t('mdata.assign.permissions')} /*分配权限*/
        visible={visible}
        onOk={this.handleOk}
        width={800}
        onCancel={this.handleCancel}
        className="list-selector role-modal"
        confirmLoading={confirmLoading}
      >
        <SearchArea
          onRef={ref => (this.searchForm = ref)}
          searchForm={searchForm}
          submitHandle={this.search}
          clearText={this.$t('mdata.reset')} /*重置*/
        />
        <div style={{ margin: '16px 0' }}>
          <Button type="primary" onClick={this.newRow}>
            {this.$t('mdata.add')}
            {/*添加*/}
          </Button>
          <Button
            style={{ marginLeft: '15px' }}
            onClick={this.saveAll}
            type={dataCache.join('') ? 'primary' : ''}
            loading={allSaveLoading}
          >
            {this.$t('mdata.save')}
            {/*保存*/}
          </Button>
        </div>
        <Table
          dataSource={dataSource}
          columns={columns}
          rowKey={record => record['id']}
          bordered
          size="middle"
          loading={loading}
          pagination={pagination}
        />
      </Modal>
    );
  }
}

export default SelectRoles;
