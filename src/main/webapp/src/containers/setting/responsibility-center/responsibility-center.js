import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import SearchArea from 'widget/search-area';
import { Button, Tabs, Popconfirm, Badge, Divider, message } from 'antd';
import 'styles/setting/responsibility-center/responsibility-center.scss';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import NewResponsibilityCenter from 'containers/setting/responsibility-center/new-responsibility-cernter';
import NewResponsibilityCenterGroup from 'containers/setting/responsibility-center/new-responsibility-cernter-group';
import ResponsibilityService from 'containers/setting/responsibility-center/responsibility-service';
import ListSelector from 'components/Widget/list-selector';
import ImporterNew from 'widget/Template/importer-new';
import ExcelExporter from 'widget/excel-exporter';
import FileSaver from 'file-saver';
import baseService from 'share/base.service';
const TabPane = Tabs.TabPane;

class ResponsibilityCenter extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      cernterSearchForm: [
        {
          type: 'select',
          options: [],
          id: 'setOfBooksId',
          placeholder: '请选择',
          label: '账套',
          labelKey: 'setOfBooksName',
          valueKey: 'id',
          isRequired: true,
          event: 'setOfBookId',
          allowClear: false,
          defaultValue: props.company.setOfBooksId,
          colSpan: 6,
        },
        { type: 'input', id: 'responsibilityCenterCode', label: '责任中心代码', colSpan: '6' },
        { type: 'input', id: 'responsibilityCenterName', label: '责任中心名称', colSpan: '6' },
        {
          type: 'select',
          id: 'enabled',
          label: '状态',
          options: [{ value: true, label: '启用' }, { value: false, label: '禁用' }],
          colSpan: '6',
        },
      ],
      groupSearchForm: [
        {
          type: 'select',
          options: [],
          id: 'setOfBooksId',
          placeholder: '请选择',
          label: '账套',
          labelKey: 'setOfBooksName',
          valueKey: 'id',
          isRequired: true,
          event: 'setOfBooksId',
          allowClear: false,
          defaultValue: props.company.setOfBooksId,
          colSpan: 6,
        },
        { type: 'input', id: 'groupCode', label: '责任中心组代码', colSpan: '6' },
        { type: 'input', id: 'groupName', label: '责任中心组名称', colSpan: '6' },
        {
          type: 'select',
          id: 'enabled',
          label: '状态',
          options: [{ value: true, label: '启用' }, { value: false, label: '禁用' }],
          colSpan: '6',
        },
      ],
      cernterColumns: [
        {
          title: '责任中心代码',
          dataIndex: 'responsibilityCenterCode',
        },
        {
          title: '责任中心名称',
          dataIndex: 'responsibilityCenterName',
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          render: record => (
            <Badge
              status={record ? 'success' : 'error'}
              text={record ? this.$t('common.status.enable') : this.$t('common.status.disable')}
            />
          ),
        },
        {
          title: this.$t('common.operation'), //"操作",
          dataIndex: 'operate',
          render: (text, record) => (
            <span>
              {/*编辑*/}
              <a onClick={e => this.editCenterItem(record)}>{this.$t('common.edit')}</a>
              <Divider type="vertical" />
              {/*公司分配*/}
              <a onClick={e => this.assignCompany(record)}>公司分配</a>
            </span>
          ),
        },
      ],
      groupColumns: [
        {
          title: '责任中心组代码',
          dataIndex: 'groupCode',
        },
        {
          title: '责任中心组名称',
          dataIndex: 'groupName',
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          render: record => (
            <Badge
              status={record ? 'success' : 'error'}
              text={record ? this.$t('common.status.enable') : this.$t('common.status.disable')}
            />
          ),
        },
        {
          title: '添加责任中心',
          dataIndex: 'addResponsibility',
          render: (text, record) => (
            <span>
              {/**添加责任中心 */}
              <a onClick={e => this.addResponsibilityCenter(record)}>添加责任中心</a>
            </span>
          ),
        },
        {
          title: this.$t('common.operation'), //"操作",
          dataIndex: 'operate',
          render: (text, record) => (
            <span>
              {/*编辑*/}
              <a onClick={e => this.editGroupItem(record)}>{this.$t('common.edit')}</a>
              <Divider type="vertical" />
              {/*删除*/}
              <Popconfirm
                placement="top"
                title={
                  JSON.stringify(record.responsibilityCenterIdList) !== '[]'
                    ? '责任中心组下存在关联的责任中心，是否确认删除？'
                    : '确认删除？'
                }
                onConfirm={e => {
                  e.preventDefault();
                  this.deleteGroupCost(record);
                }}
                okText="确定"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm>
            </span>
          ),
        },
      ],
      //是否允许分配公司按钮可用
      ableToAllocate: true,
      selectedRowKeys: [],
      tabVal: 'cernter',
      record: {},
      showCernterSlideFrame: false,
      showGroupSlideFrame: false,
      setOfBooksId: props.company.setOfBooksId,
      searchParams: {},
      searchGropParam: {},
      isNew: false,
      centerVisible: false,
      centerItem: {},
      addCenterItem: {},
      centerParams: {},
      groupParam: {},
      //添加责任中心显示模态框
      addCenterVisible: false,
      AddCenterList: [],
      // 批量分配公司按钮显示模态框
      companyButVisible: false,
      //当前责任组数据
      curResGroupRecord: {},
      companyButItem: {},
      //当前责任中心数据
      curResRecord: {},
      // 首页批量添加分配公司
      companyBaseRecord: {},
      importerVisible: false,
      excelVisible: false,
      exportColumns: [
        { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
        { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
        { title: '状态', dataIndex: 'enabled' },
      ],
      selectedData: [],
    };
  }
  // 生命周期
  componentDidMount() {
    this.getSetOfBookList();
  }

  //获取账套列表
  getSetOfBookList = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        let list = [];
        res.data.map(item => {
          list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
        });
        let form = this.state.cernterSearchForm;
        let formGroup = this.state.groupSearchForm;
        form[0].options = list;
        formGroup[0].options = list;
        form[0].defaultValue = this.props.match.params.setOfBooksId
          ? this.props.match.params.setOfBooksId
          : this.props.company.setOfBooksId;
        formGroup[0].defaultValue = this.props.match.params.setOfBooksId
          ? this.props.match.params.setOfBooksId
          : this.props.company.setOfBooksId;
        this.setState({
          cernterSearchForm: form,
          groupSearchForm: formGroup,
          setOfBooksId: form[0].defaultValue,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ saveLoading: false });
      });
  };

  // 责任中心搜索框事件
  handleEventCenter = (event, value) => {
    if (event == 'setOfBookId') {
      this.setState(
        { setOfBooksId: value, searchParams: { ...this.state.searchParams, setOfBooksId: value } },
        () => {
          this.centerTable.search(this.state.searchParams);
        }
      );
    } else if (event == 'enabled') {
      this.setState({ searchParams: { ...this.state.searchParams, enabled: value } }, () => {
        this.centerTable.search(this.state.searchParams);
      });
    }
  };

  // 责任中心组搜索框事件
  handleEventGroup = (event, value) => {
    if (event == 'setOfBooksId') {
      this.setState(
        { setOfBooksId: value, searchParams: { ...this.state.searchParams, setOfBooksId: value } },
        () => {
          this.groupTable.search(this.state.searchParams);
        }
      );
    } else if (event == 'enabled') {
      this.setState({ searchParams: { ...this.state.searchParams, enabled: value } }, () => {
        this.groupTable.search(this.state.searchParams);
      });
    }
  };

  /**tabs切换事件 */
  onChangeTab = key => {
    this.setState({
      tabVal: key,
    });
  };

  /**责任中心搜索，清空，表格操作，新建等功能 */
  searhCernter = values => {
    values.setOfBooksId = values.setOfBooksId
      ? values.setOfBooksId.split('-')[0]
      : this.props.company.setOfBooksId;
    (values.responsibilityCenterCode = values.responsibilityCenterCode
      ? values.responsibilityCenterCode
      : undefined),
      (values.responsibilityCenterName = values.responsibilityCenterName
        ? values.responsibilityCenterName
        : undefined),
      (values.enabled = values.enabled ? values.enabled : undefined),
      this.setState(
        {
          searchParams: values,
          setOfBooksId: values.setOfBooksId.split('-')[0],
        },
        () => {
          this.centerTable.search(this.state.searchParams);
        }
      );
  };

  clearCenter = () => {
    this.setState({ searchParams: {} }, () => {
      this.centerTable.search(this.state.searchParams);
    });
  };

  //编辑责任心中单条数据
  editCenterItem = record => {
    this.setState(
      {
        isNew: false,
        centerParams: JSON.parse(JSON.stringify(record)),
      },
      () => {
        this.setState({ showCernterSlideFrame: true });
      }
    );
  };
  // 跳转分配公司 （gs）
  assignCompany = record => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/admin-setting/responsibility-center/distrbute-company/${record.id}`,
      })
    );
  };

  cancelAssignCompany = flag => {
    this.setState({ centerVisible: flag });
  };

  // 添加责任中心按钮（gs）
  addResponsibility = flag => {
    this.setState({
      addCenterVisible: flag,
      curResGroupRecord: {},
      addCenterItem: {},
    });
  };
  addResponsibilityCenter = record => {
    const { setOfBooksId } = this.state;
    let addCenterItem = {
      title: '添加责任中心',
      url: `${config.mdataUrl}/api/responsibilityCenter/query/by/groupId?groupId=${
        record.id
      }&setOfBooksId=${setOfBooksId}`,
      searchForm: [
        {
          type: 'input',
          id: 'responsibilityCenterCode',
          label: '责任中心代码',
          colSpan: '8',
        },
        {
          type: 'input',
          id: 'responsibilityCenterName',
          label: '责任中心名称',
          colSpan: '8',
        },
        {
          type: 'select',
          id: 'enabled',
          label: '状态',
          options: [{ value: true, label: '启用' }, { value: false, label: '禁用' }],
          colSpan: '8',
        },
        {
          type: 'select',
          id: 'range',
          label: '查看',
          options: [
            { value: 'ALL', label: '全选' },
            { value: 'SELECTED', label: '已选' },
            { value: 'NOTCHOOSE', label: '未选' },
          ],
          colSpan: '8',
          defaultValue: record.rang,
        },
      ],
      columns: [
        { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
        { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
        {
          title: '状态',
          dataIndex: 'enabled',
          render: (enabled, record, index) => {
            return (
              <Badge status={enabled ? 'success' : 'error'} text={enabled ? '启用' : '禁用'} />
            );
          },
        },
      ],
      key: 'id',
    };

    let temp =
      record.responsibilityCenterIdList &&
      record.responsibilityCenterIdList.map(item => ({ id: item }));
    this.setState({
      addCenterVisible: true,
      addCenterItem,
      curResGroupRecord: record,
      selectedData: temp,
    });
  };

  // 责任中心组添加责任中心（gs）
  handleAddCenter = value => {
    let { curResGroupRecord } = this.state;
    let temp = value.result.map(centerItem => {
      return centerItem.id;
    });
    ResponsibilityService.addResponsibilityCenterToGroup(curResGroupRecord.id, temp)
      .then(res => {
        message.success('添加成功');
        this.setState({ addCenterVisible: false }, () => {
          this.groupTable.search(this.state.searchGropParam);
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //勾选责任中心，多选(gs)
  onSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys }, () => {
      selectedRowKeys.length > 0
        ? this.setState({ ableToAllocate: false })
        : this.setState({ ableToAllocate: true });
    });
  };

  //新建责任中心
  addCenter = () => {
    this.setState(
      {
        centerParams: { setOfBooksId: this.state.setOfBooksId },
        isNew: true,
      },
      () => {
        this.setState({ showCernterSlideFrame: true });
      }
    );
  };

  handleCloseCenterSlide = () => {
    this.setState({ showCernterSlideFrame: false }, () => {
      this.centerTable.search(this.state.searchParams);
    });
  };

  /**责任中心组搜索，清空，表格操作，新建等功能 */
  searhGroup = values => {
    values.setOfBooksId = values.setOfBooksId
      ? values.setOfBooksId.split('-')[0]
      : this.props.company.setOfBooksId;
    values.groupCode = values.groupCode ? values.groupCode : undefined;
    values.groupName = values.groupName ? values.groupName : undefined;
    values.enabled = values.enabled ? values.enabled : undefined;
    this.setState({ searchGropParam: values }, () => {
      this.groupTable.search(this.state.searchGropParam);
    });
  };

  clearGroup = () => {
    this.setState({ searchGropParam: {} }, () => {
      this.groupTable.search(this.state.searchGropParam);
    });
  };

  //新建责任中心组
  addCenterGroup = () => {
    this.setState(
      {
        groupParam: { setOfBooksId: this.state.setOfBooksId },
        isNew: true,
      },
      () => {
        this.setState({ showGroupSlideFrame: true });
      }
    );
  };

  //编辑责任中心组
  editGroupItem = record => {
    this.setState(
      {
        isNew: false,
        groupParam: JSON.parse(JSON.stringify(record)),
      },
      () => {
        this.setState({ showGroupSlideFrame: true });
      }
    );
  };

  handleCloseGroupSlide = () => {
    this.setState({ showGroupSlideFrame: false }, () => {
      this.groupTable.search(this.state.searchGropParam);
    });
  };

  //删除责任中心组
  deleteGroupCost = record => {
    ResponsibilityService.deleteResponsibilityGroup(record.id)
      .then(res => {
        message.success('删除成功！');
        this.groupTable.search(this.state.searchGropParam);
      })
      .catch(err => {
        message.error('删除失败！');
      });
  };

  // 批量分配公司按钮(gs)
  addBatchCompany = () => {
    const companyButItem = {
      title: '批量分配公司',
      url: `${config.mdataUrl}/api/responsibilityCenter/company/assign/filter/by/setOfBooksId`,
      searchForm: [
        { type: 'input', id: 'companyCode', label: '公司代码' },
        { type: 'input', id: 'companyName', label: '公司名称' },
        { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
        { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
      ],
      columns: [
        { title: '公司代码', dataIndex: 'companyCode' },
        { title: '公司名称', dataIndex: 'name' },
        { title: '公司类型', dataIndex: 'companyTypeName', render: value => (value ? value : '-') },
      ],
      key: 'id',
    };
    this.setState({
      companyButVisible: true,
      companyButItem,
    });
  };

  // 首页的批量分配公司（gs）
  onCompanyOk = value => {
    const rowKeys = this.state.selectedRowKeys;
    const params = [];
    value.result.map(item => {
      rowKeys.map(row => {
        params.push({
          companyId: item.id,
          companyCode: item.companyCode,
          responsibilityCenterId: row,
        });
      });
    });
    ResponsibilityService.companyBatch(params)
      .then(res => {
        message.success('分配成功');
        this.setState({
          companyButVisible: false,
          selectedRowKeys: [],
          ableToAllocate: true,
        });
      })
      .catch(err => {
        this.setState({
          companyButVisible: false,
          selectedRowKeys: [],
          ableToAllocate: true,
        });
        message.error(err.response.data.message);
      });
  };

  // 确认导入
  onConfirmImport = transactionId => {
    ResponsibilityService.confirmImporter(transactionId)
      .then(() => {
        this.setState({ importerVisible: false });
        this.centerTable.search();
        message.success('导入成功');
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ importerVisible: false });
      });
  };

  // 确认导出
  confirmExport = result => {
    let hide = message.loading('正在生成文件，请等待......');
    const { setOfBooksId } = this.state;
    ResponsibilityService.exportMethod(result, setOfBooksId)
      .then(res => {
        if (res.status === 200) {
          message.success('导出成功');
          let fileName = res.headers['content-disposition'].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          hide();
        }
      })
      .catch(err => {
        message.error('下载失败，请重试!');
        hide();
      });
  };

  render() {
    const {
      cernterSearchForm,
      cernterColumns,
      selectedRowKeys,
      isNew,
      centerParams,
      tabVal,
      groupSearchForm,
      groupColumns,
      showCernterSlideFrame,
      showGroupSlideFrame,
      setOfBooksId,
      addCenterVisible,
      addCenterItem,
      companyButVisible,
      record,
      groupParam,
      companyButItem,
      excelVisible,
      importerVisible,
      exportColumns,
      ableToAllocate,
      selectedData,
    } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    return (
      <div>
        <Tabs defaultActiveKey={tabVal} onChange={this.onChangeTab}>
          <TabPane tab="责任中心" key="cernter">
            {tabVal === 'cernter' && (
              <div>
                <SearchArea
                  searchForm={cernterSearchForm}
                  submitHandle={this.searhCernter}
                  extraParams={{ setOfBooksId }}
                  clearHandle={this.clearCenter}
                  maxLength={4}
                  eventHandle={this.handleEventCenter}
                />
                <div className="btnMargin">
                  <Button type="primary" onClick={this.addCenter}>
                    新建
                  </Button>
                  <Button type="primary" disabled={ableToAllocate} onClick={this.addBatchCompany}>
                    批量分配公司
                  </Button>
                  <Button type="primary" onClick={() => this.setState({ importerVisible: true })}>
                    导入
                  </Button>
                  <Button type="primary" onClick={() => this.setState({ excelVisible: true })}>
                    导出
                  </Button>
                </div>
                <div style={{ marginTop: 10 }}>
                  <CustomTable
                    columns={cernterColumns}
                    url={`${config.mdataUrl}/api/responsibilityCenter/query`}
                    params={{ setOfBooksId: setOfBooksId }}
                    ref={ref => (this.centerTable = ref)}
                    rowSelection={rowSelection}
                  />
                </div>
              </div>
            )}
            <SlideFrame
              title={isNew ? '新建责任中心' : '编辑责任中心'}
              show={showCernterSlideFrame}
              onClose={() => this.setState({ showCernterSlideFrame: false })}
            >
              <NewResponsibilityCenter
                setOfBooks={cernterSearchForm[0].options}
                params={centerParams}
                close={this.handleCloseCenterSlide}
                set={setOfBooksId}
              />
            </SlideFrame>
          </TabPane>
          <TabPane tab="责任中心组" key="cernterGroup">
            {tabVal === 'cernterGroup' && (
              <div>
                <SearchArea
                  searchForm={groupSearchForm}
                  submitHandle={this.searhGroup}
                  clearHandle={this.clearGroup}
                  maxLength={4}
                  eventHandle={this.handleEventGroup}
                />
                <div className="btnMargin">
                  <Button type="primary" onClick={this.addCenterGroup}>
                    新建
                  </Button>
                </div>
                <div style={{ marginTop: 10 }}>
                  <CustomTable
                    columns={groupColumns}
                    url={`${config.mdataUrl}/api/responsibilityCenter/group/query`}
                    params={{ setOfBooksId: setOfBooksId }}
                    ref={ref => (this.groupTable = ref)}
                  />
                </div>
              </div>
            )}
            <SlideFrame
              title={isNew ? '新建责任中心组' : '编辑责任中心组'}
              show={showGroupSlideFrame}
              onClose={() => this.setState({ showGroupSlideFrame: false })}
            >
              <NewResponsibilityCenterGroup
                setOfBooks={groupSearchForm[0].options}
                params={groupParam}
                set={setOfBooksId}
                close={this.handleCloseGroupSlide}
              />
            </SlideFrame>
            {/* 添加责任中心 */}
            <ListSelector
              visible={addCenterVisible}
              selectorItem={addCenterItem}
              onOk={this.handleAddCenter}
              labelKey={''}
              valueKey={'id'}
              selectedData={selectedData} //
              onCancel={() => this.addResponsibility(false)}
              showSelectTotal={true}
              single={false}
            />
          </TabPane>
        </Tabs>
        {/* 批量公司按钮 */}
        <ListSelector
          visible={companyButVisible}
          onCancel={() => {
            this.setState({ companyButVisible: false });
          }}
          onOk={this.onCompanyOk}
          selectorItem={companyButItem}
          extraParams={{
            responsibilityCenterId: record.id,
            setOfBooksId: this.props.company.setOfBooksId,
          }}
          single={false}
          showSelectTotal={true}
        />
        {/* {导入} */}
        <ImporterNew
          visible={importerVisible}
          templateUrl={`${config.mdataUrl}/api/responsibilityCenter/template`}
          deleteDataUrl={`${config.mdataUrl}/api/responsibilityCenter/import/delete`}
          errorUrl={`${config.mdataUrl}/api/responsibilityCenter/import/error/export`}
          errorDataQueryUrl={`${config.mdataUrl}/api/responsibilityCenter/import/query/result`}
          uploadUrl={`${
            config.mdataUrl
          }/api/responsibilityCenter/import?setOfBooksId=${setOfBooksId}`}
          fileName="责任中心导入模板"
          afterClose={() => {
            this.setState({ importerVisible: false });
          }}
          onOk={this.onConfirmImport}
        />
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName="责任中心"
          onCancel={() => this.setState({ excelVisible: false })}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
        />
      </div>
    );
  }
}

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
)(ResponsibilityCenter);
