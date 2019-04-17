import React, { Component } from 'react';
import { Input, Icon, Popconfirm, Button, message, Modal, Popover } from 'antd';
import 'styles/workbench/work-group-definition/work-group.scss';
import WorkTeamDetails from './component/work-team-details';
import WorkTeamTree from './component/work-team-tree';
import NewWorkTeamForm from './component/new-work-team';
import SearchListTree from './component/work-team-tree/search-list-tree';
import UserSelector from './component/user-selector/user-selector';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import service from './work-group-definition-service';

const Search = Input.Search;

class WorkGroupDefine extends Component {
  constructor(props) {
    super(props);
    this.state = {
      newWorkTeamFormVisible: false,
      modelForm: {}, //新建工作组模态框数据
      useOrNotInTeamForm: false, //新建工作组模态框select控件可用
      loading: false,
      dataSources: [], //员工数据源
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      }, //分页
      page: 0,
      size: 10,
      selectedRowKeys: [], //多选框
      searchEmployeeConditions: {}, //员工搜索条件
      columns: [
        { title: this.$t('workbench.employee.code'), dataIndex: 'userCode' },
        { title: this.$t('workbench.employee.name'), dataIndex: 'userName' },
        {
          title: this.$t('acp.company'), // '公司'
          dataIndex: 'companyDisplay',
          align: 'center',
          render: (text, record, index) => {
            return record.companyDisplay ? (
              <span>
                <Popover
                  content={`${record.companyDisplay}`}
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                >
                  {record.companyDisplay}
                </Popover>
              </span>
            ) : (
              `-`
            );
          },
        },
        {
          title: this.$t('common.department') /*部门*/,
          dataIndex: 'unitDisplay',
          align: 'center',
          render: (text, record, index) => {
            return record.unitDisplay ? (
              <span>
                <Popover
                  content={`${record.unitDisplay}`}
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                >
                  {record.unitDisplay}
                </Popover>
              </span>
            ) : (
              `-`
            );
          },
        },
        {
          title: this.$t('common.operation') /*操作*/,
          dataIndex: 'operation',
          align: 'center',
          render: (operation, record, index) => {
            return (
              <Popconfirm
                placement="top"
                title={this.$t('workbench.sure.delete')}
                onConfirm={e => this.handleDelSingleValue(e, record)}
                okText={this.$t('common.yes')}
                cancelText={this.$t('common.no')}
              >
                <a> {this.$t('common.delete')}</a>
              </Popconfirm>
            );
          },
        },
      ],
      showListSelector: false, //添加员工模态框可见
      selectorItem: {
        title: this.$t('workbench.team.employee.save'), // '添加员工'
        url: `${config.mdataUrl}/api/user/simpleInfo/query`,
        searchForm: [
          { type: 'input', id: 'employeeCode', label: this.$t('workbench.employee.code') },
          { type: 'input', id: 'name', label: this.$t('workbench.employee.name') },
          {
            type: 'select',
            id: 'companyId',
            label: this.$t('acp.company'), // '公司'
            options: [],
            getUrl: `${config.mdataUrl}/api/company/by/tenant`, //集团下enabled公司
            method: 'get',
            labelKey: 'name',
            valueKey: 'id',
          },
          {
            type: 'select',
            id: 'unitId',
            label: this.$t('common.department'),
            options: [],
            getUrl: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
            method: 'get',
            labelKey: 'name',
            valueKey: 'departmentId',
          },
        ],
        columns: [
          { title: this.$t('workbench.employee.code'), dataIndex: 'employeeCode' },
          { title: this.$t('workbench.employee.name'), dataIndex: 'userName' },
          {
            title: this.$t('acp.company'), // '公司'
            dataIndex: 'companyName',
            render: value => {
              return value ? (
                <span>
                  <Popover
                    content={value}
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                  >
                    {value}
                  </Popover>
                </span>
              ) : (
                `-`
              );
            },
          },
          {
            title: this.$t('common.department'),
            dataIndex: 'departmentName',
            render: value => {
              return value ? (
                <span>
                  <Popover
                    content={value}
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                  >
                    {value}
                  </Popover>
                </span>
              ) : (
                `-`
              );
            },
          },
        ],
        method: 'post',
        key: 'userId',
      },
      treeData: [], //工作组树数据   ---res.data.treeData
      _treeData: [], //工作组树数据副本 --用于右侧详情编辑后重渲染树，避免查工作组树接口的再调用
      searchTreeConditions: {}, //工作组树搜索条件
      selectedKeys: [], //默认展示的树数据 -存放选中的工作组id
      expandedKeys: [], //展开指定的树
      autoExpandParent: true, //是否自动展开父节点
      workTeamEnabledList: [], //工作组树数据副本--用于新增，格式：[{label: '',value: ''}]
      workDetails: {}, //选中的工作组详情数据
      isEdit: 'normal', //右侧工作组详情是否可编辑
      userData: [], //查询到的员工数据tree   --- res.data.wbcUserInfoDTOList
      isSearching: false, //工作组查询结束与否
      extraParams: {}, // 存储当前工作组下已分配的员工id
      addLoading: false,
    };
  }

  componentDidMount = () => {
    this.getWorkGroupValueList('query', true, true);
  };

  /**
   * 获取工作组数据源
   * mode [ 'query' / 'search' ]: 走全查接口还是走条件查询接口
   * flag1 & flag2 [ true / false ]
   * 决定是否要重新获取详情和员工数据，避免接口的重复调用
   */
  getWorkGroupValueList = (mode, flag1, flag2) => {
    let methodToTeam = null;
    let params = {};
    if (mode == 'query') {
      methodToTeam = service.getWorkTeamList;
    } else if (mode == 'search') {
      methodToTeam = service.getWorkTeamBySearch;
      params.keyword = this.state.searchTreeConditions.keyword;
    }
    methodToTeam &&
      methodToTeam(params)
        .then(res => {
          if (mode == 'query') {
            let formatData = this.formatTeamTree(res.data);
            this.setState(
              {
                treeData: formatData,
                _treeData: res.data,
                //当工作组的状态发生变化后，重渲染树时保留上一次选中的树节点
                selectedKeys:
                  this.state.selectedKeys.length > 0
                    ? this.state.selectedKeys
                    : res.data.length > 0
                      ? [formatData[0].id]
                      : [],
              },
              () => {
                if (res.data.length < 1) {
                  this.setState({ dataSources: [], workDetails: {} });
                  return;
                } //无数据情况下不执行--写在这是考虑到仅有一条工作组被删的情况
                flag1 && this.getSelectTreeDetail(this.state.selectedKeys[0]);
                flag2 && this.getEmployeeValueLists(this.state.selectedKeys[0], 'query');
              }
            );
          } else if (mode == 'search') {
            let willSelectedKey = [];
            let temp = {}; //临时存储查询到的userData/wbcUserInfoDTOList中第一条员工信息
            if (res.data.wbcUserInfoDTOList.length > 0) {
              willSelectedKey = [res.data.wbcUserInfoDTOList[0].workTeamId];
              res.data.wbcUserInfoDTOList[0].active = true;
              temp = {
                user: res.data.wbcUserInfoDTOList[0],
                isOnly: true,
              };
            } else if (res.data.wbcBusinessWorkTeamDTOList.length > 0) {
              res.data.wbcBusinessWorkTeamDTOList[0].active = true;
              willSelectedKey = [res.data.wbcBusinessWorkTeamDTOList[0].id];
            } else willSelectedKey = [];

            this.setState(
              {
                _treeData: res.data.wbcBusinessWorkTeamDTOList,
                userData: res.data.wbcUserInfoDTOList,
                selectedKeys: willSelectedKey,
                isSearching: false,
              },
              () => {
                flag1 && this.getSelectTreeDetail(this.state.selectedKeys[0]);
                flag2 && this.getEmployeeValueLists(this.state.selectedKeys[0], 'query', temp);
              }
            );
          }
        })
        .catch(err => {
          err.response && message.error(err.response.data.message);
        });
  };
  //序列化工作树，生成子数组
  formatTeamTree = data => {
    data.forEach(item => {
      item.children = [];
    });
    data.forEach(item => {
      if (item.parentId) {
        data.forEach(value => {
          if (item.parentId == value.id) {
            value.children.push(item);
          }
        });
      }
    });
    let temp = [];
    data.forEach(item => {
      if (!item.parentId) temp.push(item);
    });
    return temp;
  };

  //渲染左侧工作树或查询后的列表
  renderTreeOrListTree = () => {
    const { searchTreeConditions } = this.state;
    if (searchTreeConditions.keyword && searchTreeConditions.keyword.length > 0) {
      return (
        <SearchListTree
          teamData={this.state._treeData}
          userData={this.state.userData}
          searchValue={this.state.searchTreeConditions.keyword}
          transferSelectedValue={this.transferSelectedValue}
          isSearching={this.state.isSearching}
        />
      );
    } else
      return (
        <WorkTeamTree
          treeData={this.state.treeData}
          selectedKeys={this.state.selectedKeys}
          expandedKeys={this.state.expandedKeys}
          autoExpandParent={this.state.autoExpandParent}
          onSelect={this.onSelectTreeValue}
          onExpand={this.onExpandTreeValue}
          handleNewWorkTeam={this.handleNewWorkTeam}
          getWorkGroupValueList={this.getWorkGroupValueList}
          changeSelectedKeys={this.changeSelectedKeys}
        />
      );
  };

  //工作组查询后从子组件将选中的数据返回
  transferSelectedValue = (selectedItem, target) => {
    let selectedId = selectedItem.id || selectedItem.workTeamId;
    //此函数的功能类似于onSlectTreeValue但前者用在查询后页面，后者用在未查询前页面；
    this.setState({
      selectedKeys: [selectedId],
    });
    this.getSelectTreeDetail(selectedId);
    //如果是选中工作组则展示该组下员工所有数据，若是员工则展示该条
    let temp = target == 'user' ? { user: { ...selectedItem }, isOnly: true } : {};
    this.getEmployeeValueLists(selectedId, 'query', temp);
  };

  //重渲染tree-右侧详情编辑后为减少接口调用执行
  reRenderTree = data => {
    let { _treeData, userData } = this.state;
    if (data.id) {
      let index = _treeData.findIndex(item => {
        return data.id == item.id;
      });
      data.active = true; //为保留SearchListTree组件中在编辑详情重渲染左侧的时候被选中的数据的样式
      _treeData[index] = JSON.parse(JSON.stringify(data));
    } else if (data.workTeamId) {
      //如果data没有id表示你在条件查询后通过点击员工数据进行工作组详情修改
      //这里需要区分考虑全查下的编辑详情和条件查询下由员工或工作组渲染右侧详情再编辑
      let index = userData.findIndex(item => {
        return data.workTeamId == item.workTeamId;
      });
      data.active = true;
      userData[index] = JSON.parse(JSON.stringify(data));
    }
    this.setState({
      treeData: this.formatTeamTree(_treeData),
      _treeData,
      userData,
      /**
       * 这一步是为了将改变后的详情值赋予一次，render右侧，
       * 否侧details控件里的workDetails虽然通过setState改变了，
       * 但由于本页面render，而workDetails值仍然是上一次的值，再次传递给子组件时，值回到上一次的情况
       *  */
      workDetails: { ...data },
    });
  };

  //删除工作组操作后---改变选中的树节点
  changeSelectedKeys = () => {
    this.setState(
      {
        selectedKeys: [],
      },
      () => {
        this.getWorkGroupValueList('query', true);
      }
    );
  };

  //选中树节点触发
  onSelectTreeValue = (selectedKeys, e) => {
    //只有当选中的treeNode与已被选中的treeNode不同的时候，重新渲染并请求详情数据改变右侧显示
    const that = this;
    if (
      JSON.stringify(selectedKeys) !== '[]' &&
      JSON.stringify(selectedKeys) !== JSON.stringify(this.state.selectedKeys)
    ) {
      // e.selectedNodes[0] && console.log(e.selectedNodes[0].props.dataRef); //选中的节点的详情值
      //当右侧详情处于编辑状态下，发生跳转时：‘yes’则将编辑改为‘normal’状态并跳转
      if (this.state.isEdit == 'edit') {
        Modal.confirm({
          title: this.$t('org.tips'),
          content: this.$t('workbench.tip-team-is-edit'),
          onOk() {
            that.confirmDispatchToJump(selectedKeys, 'yes');
          },
          onCancel() {},
        });
      } else this.confirmDispatchToJump(selectedKeys);
    }
  };

  //确认触发工作组树选中跳转
  confirmDispatchToJump = (selectedKeys, flag) => {
    //设置工作组树id作为标识selected，异步获取该组下的详情和员工表数据
    this.setState(
      {
        selectedKeys,
        isEdit: flag == 'yes' ? 'normal' : this.state.isEdit,
      },
      () => {
        this.getSelectTreeDetail(selectedKeys[0]);
        this.getEmployeeValueLists(selectedKeys[0], 'query');
      }
    );
  };

  // 查询当前工作组下员工
  getAllUser = (workTeamId, params) => {
    let id = workTeamId || '';
    if (!id) {
      this.setState({ extraParams: {}, addLoading: false });
      message.error(this.$t('workbench.desc.code')); // 错误，无法获取当前工作组id，请联系管理员！
      return;
    }
    service
      .getAllUserByWorkTeamId({ workTeamId: id })
      .then(res => {
        if (res.data.length < 1 || JSON.stringify(res.data) == '{}') {
          this.setState({ extraParams: {}, addLoading: false }, () => {
            this.setState({ ...params });
          });
          return;
        }
        let { extraParams } = this.state;
        extraParams.ids = res.data;
        this.setState({ extraParams, addLoading: false }, () => {
          this.setState({ ...params });
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //通过工作组id获取详情
  getSelectTreeDetail = workTeamId => {
    let id = workTeamId || '';
    if (!id) {
      this.setState({ workDetails: {} });
      return;
    }
    service
      .getCurWorkTeamDetail(id)
      .then(res => {
        if (res.data.length < 1 || JSON.stringify(res.data) == '{}') {
          this.setState({ workDetails: {} });
          return;
        }
        res.data.workManager =
          res.data.workManager.indexOf('-') !== -1
            ? res.data.workManager.split('-')[1]
            : res.data.workManager;
        this.setState({ workDetails: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //改变工作组是否可编辑状态
  changeDetailStatus = value => {
    this.setState({ isEdit: value });
  };

  //展示/收起树节点触发
  onExpandTreeValue = (expandedKeys, info) => {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    });
  };

  //获取员工数据源 mode: 'query' : 获取所有员工 ， 'search' : 查询获取
  getEmployeeValueLists = (workTeamId, mode, temp) => {
    let { page, size, pagination, searchEmployeeConditions } = this.state;
    this.setState({ loading: true });
    let methodToEmployee = null;
    let params = { page, size, workTeamId };
    if (mode == 'query') {
      methodToEmployee = service.getEmployeeList;
      params.keyword = '';
    } else if (mode == 'search') {
      methodToEmployee = service.getEmployeeBySearch;
      params.keyword = searchEmployeeConditions.keyword;
    }

    methodToEmployee &&
      methodToEmployee(params)
        .then(res => {
          pagination.total = Number(res.headers['x-total-count']);
          let tempList = [];
          if (temp && temp.isOnly) {
            //此时左侧工作树处于条件查询，且可查到userData,则dataSources需要展示改员工的信息
            res.data.forEach(item => {
              if (temp.user && item.userId == temp.user.userId) {
                tempList.push({ ...temp.user, id: temp.user.userId });
              }
            });
            pagination.total = 1;
          } else tempList = res.data;
          this.setState({
            // dataSources: res.data,
            dataSources: tempList,
            pagination,
            loading: false,
            searchEmployeeConditions: {},
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false, searchEmployeeConditions: {} });
        });
  };

  //搜索‘工作组名称/工作组编码/员工名称’
  handleSearch = value => {
    const { searchTreeConditions } = this.state;
    searchTreeConditions.keyword = value;
    this.setState(
      {
        isSearching: true,
        searchTreeConditions,
        selectedKeys: [],
        _treeData: [],
        userData: [],
      },
      () => {
        value
          ? this.getWorkGroupValueList('search', true, true)
          : this.getWorkGroupValueList('query', true, true);
      }
    );
  };

  //新建工作组
  handleNewWorkTeam = (level, item) => {
    /**
     * level决定模态框新增顶级，平级，下级工作组,
     * 由WorkTeamTree控件的menu按钮触发事情将工作组信息传递，并根据level决定
     * 传递给newWorkTeam控件内 '上级工作组' 字段的初始值
     */
    let useOrNotInTeamForm = false;
    let workTeamEnabledList = [];
    this.state._treeData.forEach(item => {
      if (item.enabled) {
        workTeamEnabledList.push({ label: item.workTeamName, value: item.id });
      }
    });
    let modelForm = this.state.modelForm;
    // 新增一个不启用父工作组属性；新增平级、子级工作组时不维护父工作组；直接新增工作组时可维护父工作组
    switch (level) {
      case 'peers':
        modelForm.disableParent = true;
        modelForm.parentId = item.parentId;
        modelForm.parent = item.parent;
        break; //item.parentId
      case 'lowerLevel':
        modelForm.disableParent = true;
        modelForm.parentId = item.id;
        modelForm.parent = item.workTeamName;
        break; //item.id
      default:
        modelForm.disableParent = false;
        useOrNotInTeamForm = true;
    }
    this.setState({
      newWorkTeamFormVisible: true,
      useOrNotInTeamForm,
      modelForm,
      workTeamEnabledList,
    });
  };

  //取消新建工作组
  closeSlideForm = flag => {
    this.setState(
      {
        newWorkTeamFormVisible: false,
        modelForm: {},
        useOrNotInTeamForm: false,
        searchTreeConditions: flag ? {} : this.state.searchTreeConditions,
      },
      () => {
        if (flag) {
          this.state.treeData.length > 0
            ? this.getWorkGroupValueList('query')
            : this.getWorkGroupValueList('query', true);
        }
      }
    );
  };

  //添加
  handleAddValue = () => {
    const { selectedKeys } = this.state;
    this.setState({ addLoading: true }, () => {
      this.getAllUser(selectedKeys[0], { showListSelector: true });
    });
  };
  //取消添加
  onEmployeeModalCancel = () => {
    this.setState({ showListSelector: false });
  };

  //确定添加-批量添加
  handleEmployeeListOk = value => {
    let paramsList = value.result.map(item => {
      let params = {
        workTeamId: this.state.selectedKeys[0],
        companyDisplay: item.companyName,
        companyId: item.companyId,
        departmentId: item.departmentId,
        unitDisplay: item.departmentName,
        userCode: item.employeeCode,
        userName: item.userName,
        userId: item.userId,
      };
      return params;
    });
    service
      .addEmployeeValue(paramsList)
      .then(res => {
        message.success(this.$t('structure.saveSuccess')); //'添加操作成功'
        this.setState({ showListSelector: false }, () => {
          this.getEmployeeValueLists(this.state.selectedKeys[0], 'query');
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //批量删除tip框
  dialogToBatchDelete = () => {
    const that = this;
    Modal.confirm({
      content: this.$t('workbench.sure.delete'),
      icon: <Icon type="close-circle" theme="filled" />,
      onOk() {
        that.handleBatchDelete();
      },
      onCancel() {
        that.cancelBatchDelete();
      },
    });
  };

  //批量删除
  handleBatchDelete = () => {
    //批量删除传递id数组,与workTeamId
    service
      .batchDeleteEmployeeValue(this.state.selectedRowKeys)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.setState(
          {
            selectedRowKeys: [],
          },
          () => {
            this.getEmployeeValueLists(this.state.selectedKeys[0], 'query');
          }
        );
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //取消批量删除
  cancelBatchDelete = () => {
    this.setState({ selectedRowKeys: [] });
  };

  //删除
  handleDelSingleValue = (e, record) => {
    e.preventDefault();
    let workTeamId = this.state.selectedKeys[0];
    service
      .deleteEmployeeValue(workTeamId, record.id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.getEmployeeValueLists(workTeamId, 'query');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //查询员工 byCode or name [字段：keyword]
  handleSearchByCodeName = value => {
    const searchEmployeeConditions = this.state.searchEmployeeConditions;
    searchEmployeeConditions.keyword = value;
    this.setState({ searchEmployeeConditions }, () => {
      value
        ? this.getEmployeeValueLists(this.state.selectedKeys[0], 'search')
        : this.getEmployeeValueLists(this.state.selectedKeys[0], 'query');
    });
  };

  //分页
  tablePageChange = pagination => {
    this.setState(
      {
        page: pagination.current - 1,
        size: pagination.pageSize || 10,
      },
      () => {
        this.getEmployeeValueLists(this.state.selectedKeys[0], 'query');
      }
    );
  };

  //多选
  onSelectChange = value => {
    this.setState({ selectedRowKeys: value });
  };

  render() {
    const {
      dataSources,
      loading,
      selectedRowKeys,
      columns,
      pagination,
      selectorItem,
      showListSelector,
      modelForm,
      newWorkTeamFormVisible,
      workTeamEnabledList,
      useOrNotInTeamForm,
      workDetails,
      extraParams,
      addLoading,
    } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    return (
      <div className="work-group-define">
        <div className="f-left work-group-define-left clear">
          <Search
            placeholder={this.$t('workbench.team.name-code-eName')}
            allowClear
            enterButton={
              <span>
                {/*搜索*/}
                {this.$t('org.search')}
              </span>
            }
            onSearch={this.handleSearch}
          />
          <Button onClick={this.handleNewWorkTeam} type="primary" style={{ margin: '10px 0' }}>
            {/* 新建工作组 */}
            {this.$t('workbench.team.new')}
          </Button>
          {/* 工作组树节点 */}
          {this.renderTreeOrListTree()}
        </div>
        <div className="work-group-define-right">
          <div className="details">
            <WorkTeamDetails
              selectorItem={selectorItem}
              workDetails={workDetails}
              changeStatus={this.changeDetailStatus}
              status={this.state.isEdit}
              reRenderTree={this.reRenderTree}
            />
          </div>
          <div className="operation">
            <Button
              type="primary"
              loading={addLoading}
              onClick={this.handleAddValue}
              style={{ marginRight: '10px' }}
            >
              {/* 添加 */}
              {this.$t('common.add')}
            </Button>
            <Button
              disabled={selectedRowKeys.length > 0 ? false : true}
              type="primary"
              onClick={this.dialogToBatchDelete}
            >
              {/* 删除 */}
              {this.$t('common.delete')}
            </Button>
            <Search
              style={{ float: 'right', width: '240px' }}
              placeholder={this.$t('workbench.workTeam.eName.eCode.please.enter')} // "请输入员工工号/姓名"
              allowClear
              onSearch={this.handleSearchByCodeName}
            />
          </div>
          <Table
            columns={columns}
            rowKey={record => record.id}
            size="middle"
            bordered
            dataSource={dataSources}
            loading={loading}
            pagination={pagination}
            onChange={this.tablePageChange}
            rowSelection={rowSelection}
          />
          {/* 添加员工 */}
          <UserSelector
            visible={showListSelector}
            onCancel={this.onEmployeeModalCancel}
            selectorItem={selectorItem}
            onOk={this.handleEmployeeListOk}
            extraParams={extraParams}
            method="post"
            showSelectTotal
          />
        </div>
        <SlideFrame
          title={this.$t('workbench.team.new')}
          show={newWorkTeamFormVisible}
          onClose={() => {
            this.closeSlideForm(false);
          }}
        >
          <NewWorkTeamForm
            params={modelForm}
            close={this.closeSlideForm}
            useOrNot={useOrNotInTeamForm}
            workTeamEnabledList={workTeamEnabledList}
          />
        </SlideFrame>
      </div>
    );
  }
}

export default WorkGroupDefine;
