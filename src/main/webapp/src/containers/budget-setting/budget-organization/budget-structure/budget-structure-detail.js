/**
 *  created by jsq on 2017/9/20
 */
import React from 'react';
import { connect } from 'dva';
import budgetService from 'containers/budget-setting/budget-organization/budget-structure/budget-structure.service';
import config from 'config';
import {
  Form,
  Button,
  Select,
  Input,
  Spin,
  Icon,
  Badge,
  Tabs,
  Checkbox,
  message,
  Popover,
  Radio,
} from 'antd';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import 'styles/budget-setting/budget-organization/budget-structure/budget-structure-detail.scss';
import SlideFrame from 'widget/slide-frame';
import NewDimension from 'containers/budget-setting/budget-organization/budget-structure/new-dimension';
import UpdateDimension from 'containers/budget-setting/budget-organization/budget-structure/update-dimension';
import ListSelector from 'widget/list-selector';
import BasicInfo from 'widget/basic-info';
import Chooser from 'components/Widget/chooser';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
const Search = Input.Search;
const Option = Select.Option;

let periodStrategy = [];
let btnFlag = true;

class BudgetStructureDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      //添加公司弹框
      lov: {
        type: 'company_structure', //lov类型
        visible: false, //控制是否弹出
      },
      updateState: false,
      structure: {},
      dimension: {},
      showSlideFrame: false,
      showSlideFrameUpdate: false,
      statusCode: this.$t({ id: 'common.status.enable' }) /*启用*/,
      total: 0,
      data: [],
      pagination: {
        current: 1,
        page: 0,
        total: 0,
        pageSize: 10,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      label: 'response',
      columns: [],
      infoList: [
        {
          type: 'input',
          id: 'organizationName',
          isRequired: true,
          disabled: true,
          label: this.$t({ id: 'budget.organization' }) + ' :' /*预算组织*/,
        },
        {
          type: 'input',
          id: 'structureCode',
          isRequired: true,
          disabled: true,
          label: this.$t({ id: 'budget.structureCode' }) + ' :' /*预算表代码*/,
        },
        {
          type: 'input',
          id: 'structureName',
          isRequired: true,
          label: this.$t({ id: 'budget.structureName' }) + ' :' /*预算表名称*/,
        },
        {
          type: 'select',
          options: periodStrategy,
          isRequired: true,
          id: 'periodStrategy',
          label: this.$t({ id: 'budget.periodStrategy' }) + ' :' /*编制期段*/,
        },
        {
          type: 'input',
          id: 'description',
          label: this.$t({ id: 'budget.structureDescription' }) + ' :' /*预算表描述*/,
        },
        {
          type: 'switch',
          id: 'enabled',
          label: this.$t({ id: 'common.column.status' }) + ' :' /*状态*/,
        },
      ],
      columnGroup: {
        company: [
          {
            /*公司代码*/
            title: this.$t({ id: 'structure.companyCode' }),
            key: 'companyCode',
            dataIndex: 'companyCode',
          },
          {
            /*公司名称*/
            title: this.$t({ id: 'structure.companyName' }),
            key: 'companyName',
            dataIndex: 'companyName',
          },
          {
            /*公司类型*/
            title: this.$t({ id: 'structure.companyType' }),
            key: 'companyTypeName',
            dataIndex: 'companyTypeName',
            render: recode => <span>{recode ? recode : '-'}</span>,
          },
          {
            /*启用*/
            title: this.$t({ id: 'structure.enablement' }),
            key: 'doneRegisterLead',
            align: 'center',
            dataIndex: 'doneRegisterLead',
            width: '10%',
            render: (enabled, record) => (
              <Checkbox onChange={e => this.onChangeEnabled(e, record)} checked={record.enabled} />
            ),
          },
        ],
        dimension: [
          {
            /*维度代码*/
            title: this.$t({ id: 'structure.dimensionCode' }),
            key: 'dimensionCode',
            dataIndex: 'dimensionCode',
          },
          {
            /*维度名称*/
            title: this.$t({ id: 'structure.dimensionName' }),
            key: 'dimensionName',
            dataIndex: 'dimensionName',
            render: record => (
              <span>{record ? <Popover content={record}>{record} </Popover> : '-'} </span>
            ),
          },
          {
            /*布局位置*/
            title: this.$t({ id: 'structure.layoutPosition' }),
            key: 'layoutPositionName',
            dataIndex: 'layoutPositionName',
          },
          {
            /*布局顺序*/
            title: this.$t({ id: 'structure.layoutPriority' }),
            key: 'layoutPriority',
            dataIndex: 'layoutPriority',
          },
          {
            /*默认维值代码*/
            title: this.$t({ id: 'structure.defaultDimValueCode' }),
            key: 'defaultDimValueCode',
            dataIndex: 'defaultDimValueCode',
            render: recode => <span>{recode ? recode : '-'}</span>,
          },
          {
            /*默认维值名称*/
            title: this.$t({ id: 'structure.defaultDimValueName' }),
            key: 'defaultDimValueName',
            dataIndex: 'defaultDimValueName',
            render: record => (
              <span>{record ? <Popover content={record}>{record} </Popover> : '-'} </span>
            ),
          },
          {
            title: this.$t({ id: 'common.column.status' }),
            dataIndex: 'enabled',
            width: '15%',
            render: enabled => (
              <Badge
                status={enabled ? 'success' : 'error'}
                text={
                  enabled
                    ? this.$t({ id: 'common.status.enable' })
                    : this.$t({ id: 'common.disabled' })
                }
              />
            ),
          }, //状态
        ],
      },
      tabs: [
        {
          key: 'response',
          name: this.$t({ id: 'structure.responsibilityCenterSetting' }),
        } /*责任中心设置*/,
        { key: 'dimension', name: this.$t({ id: 'structure.dimensionDistribute' }) } /*维度分配*/,
        { key: 'company', name: this.$t({ id: 'structure.companyDistribute' }) } /*公司分配*/,
      ],
      saveLoading: false,
      setting: {},
      spinning: false,
      defaultList: [],
      defaultListCache: [],
    };
  }
  //改变启用状态
  onChangeEnabled = (e, record) => {
    this.setState({ loading: true });
    record.enabled = e.target.checked;
    budgetService.updateStructureAssignCompany(record).then(() => {
      this.getList();
    });
  };

  componentWillMount() {
    //获取编制期段
    !periodStrategy.length &&
      this.getSystemValueList(2002).then(response => {
        response.data.values.map(item => {
          let options = {
            label: item.name,
            value: item.value,
          };
          periodStrategy.addIfNotExist(options);
        });
      });

    //获取某预算表某行的数据
    budgetService.getStructureById(this.props.match.params.id).then(response => {
      let periodStrategy = {
        label: response.data.periodStrategyName,
        value: response.data.periodStrategy,
      };
      response.data.periodStrategy = periodStrategy;
      if (response.status === 200) {
        this.setState({
          columns: this.state.columnGroup.dimension,
          structure: response.data,
        });
        let infoList = this.state.infoList;
        infoList[3].disabled = response.data.usedFlag;
      }
    });
    this.getList();
  }

  //保存所做的修改
  handleUpdate = value => {
    value.id = this.state.structure.id;
    value.versionNumber = this.state.structure.versionNumber;
    value.organizationId = this.state.structure.organizationId;
    budgetService
      .updateStructures(value)
      .then(response => {
        if (response.status === 200) {
          let structure = response.data;
          structure.organizationName = this.state.structure.organizationName;
          message.success(this.$t({ id: 'structure.saveSuccess' })); /*保存成功！*/
          structure.periodStrategy = {
            label: response.data.periodStrategyName,
            value: response.data.periodStrategy,
          };
          structure.usedFlag = this.state.structure.usedFlag;
          this.setState(
            {
              structure: structure,
              updateState: true,
            },
            () => this.getList()
          );
        }
      })
      .catch(e => {
        if (e.response) {
          message.error(`${this.$t({ id: 'common.operate.filed' })}, ${e.response.data.message}`);
        }
      });
  };

  renderTabs() {
    return this.state.tabs.map(tab => {
      return <TabPane tab={tab.name} key={tab.key} />;
    });
  }

  //Tabs点击
  onChangeTabs = key => {
    let columnGroup = this.state.columnGroup;
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.pageSize = 10;
    this.setState(
      {
        loading: true,
        pagination,
        data: [],
        label: key,
        columns: key === 'company' ? columnGroup.company : columnGroup.dimension,
      },
      () => {
        this.getList();
      }
    );
  };

  handleCreate = e => {
    this.state.label === 'company' ? this.showListSelector(true) : this.showSlide(true);
  };

  getList = () => {
    const { pagination, label } = this.state;
    let params = {
      structureId: this.props.match.params.id,
      page: pagination.page,
      size: pagination.pageSize,
    };
    if (label === 'company') {
      budgetService.getCompanyAssignedStructure(params).then(response => {
        if (response.status === 200) {
          response.data.map(item => {
            item.key = item.id;
          });
          let pagination = this.state.pagination;
          pagination.total = Number(response.headers['x-total-count']);
          this.setState({
            loading: false,
            data: response.data,
            pagination,
          });
        }
      });
    } else if (label === 'dimension') {
      budgetService.getDimensionAssignedStructure(params).then(response => {
        if (response.status === 200) {
          response.data.map(item => {
            item.key = item.id;
          });
          let pagination = this.state.pagination;
          pagination.total = Number(response.headers['x-total-count']);
          this.setState({
            loading: false,
            data: response.data,
            pagination,
          });
        }
      });
    } else {
      this.setState({ spinning: true });
      budgetService
        .getResponsibilityCenter(this.props.match.params.id)
        .then(res => {
          const budgetStructure = res.data.budgetStructure;
          let setting = budgetStructure.responsibilityCenterRequired ? res.data : {};
          let defaultList = res.data.responsibilityCenterList;
          this.setState(
            {
              setting,
              spinning: false,
              defaultList,
            },
            () => {
              this.props.form.resetFields();
              btnFlag = true;
              const flag = budgetStructure.responsibilityCenterRange === '3';
              this.getResponsibleCenter(flag);
            }
          );
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ spinning: false });
        });
    }
  };

  // 或者账套下的 责任中心
  getResponsibleCenter = flag => {
    budgetService
      .getResponsibleCenter({ setOfBooksId: this.props.organization.setOfBooksId })
      .then(res => {
        const { defaultList } = this.state;
        this.setState({ defaultListCache: res.data, defaultList: flag ? res.data : defaultList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  //控制新建维度侧滑
  showSlide = flag => {
    this.setState({
      showSlideFrame: flag,
    });
  };

  //处理关闭新建侧滑维度页面
  handleCloseSlide = params => {
    this.setState({
      showSlideFrame: false,
      loading: typeof params === 'undefined' ? false : true,
    });
    if (params) {
      this.getList();
    }
  };

  //点击行，进入维度编辑页面
  handleRowClick = (record, index, event) => {
    if (this.state.label !== 'company') {
      let defaultDimensionCode = [];
      let defaultDimensionValue = [];
      defaultDimensionCode.push({
        dimensionId: record.dimensionId,
        dimensionCode: record.dimensionCode,
        key: record.dimensionId,
      });
      record.defaultDimValueId &&
        defaultDimensionValue.push({
          defaultDimValueId: record.defaultDimValueId,
          defaultDimValueCode: record.defaultDimValueCode,
          key: record.defaultDimValueId,
        });
      record.usedFlag = this.state.structure.usedFlag;
      record.defaultDimensionCode = defaultDimensionCode;
      record.defaultDimensionValue = defaultDimensionValue;
      this.setState({
        showSlideFrameUpdate: true,
        dimension: record,
      });
    }
  };

  showSlideUpdate = flag => {
    this.setState({
      showSlideFrameUpdate: flag,
    });
  };

  //关闭新建侧滑维度页面
  handleCloseSlideUpdate = params => {
    this.setState({
      showSlideFrameUpdate: false,
      loading: typeof params === 'undefined' ? false : true,
    });
    if (params) {
      this.getList();
    }
  };

  //控制是否弹出公司列表
  showListSelector = flag => {
    let lov = this.state.lov;
    lov.visible = flag;
    this.setState({ lov });
  };

  //处理公司弹框点击ok,分配公司
  handleListOk = result => {
    let company = [];
    result.result.map(item => {
      company.push({
        companyCode: item.companyCode,
        companyId: item.id,
        structureId: this.props.match.params.id,
        enabled: item.enabled,
      });
    });
    budgetService
      .structureAssignCompany(company)
      .then(response => {
        if (response.status === 200) {
          this.showListSelector(false);
          this.setState(
            {
              data: response.data,
            },
            () => this.getList()
          );
        }
      })
      .catch(e => {
        if (e.response) {
          message.error(`${this.$t({ id: 'common.operate.filed' })}, ${e.response.data.message}`);
        }
        this.setState({ loading: false });
      });
  };

  //返回预算表页面
  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/budget-setting/budget-organization/budget-organization-detail/:setOfBooksId/:id/:tab'
          .replace(':id', this.props.match.params.orgId)
          .replace(':setOfBooksId', this.props.match.params.setOfBooksId)
          .replace(':tab', 'STRUCTURE'),
      })
    );
  };

  //分页点击
  onChangePager = (pagination, filters, sorter) => {
    let temp = this.state.pagination;
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        loading: true,
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  // 责任中心范围改变
  rangChange = e => {
    this.props.form.setFieldsValue({ responsibilityCenterIdList: [] });
    this.props.form.setFieldsValue({ defaultResponsibilityCenterId: undefined });
    if (e.target.value === '3') {
      const { defaultListCache } = this.state;
      this.setState({ defaultList: defaultListCache });
    } else {
      this.setState({ defaultList: [] });
    }
  };

  // 可用责任中心改变
  onResponsibilityChange = value => {
    this.setState({ defaultList: value });
    this.props.form.setFieldsValue({ defaultResponsibilityCenterId: undefined });
  };

  // 责任中心设置保存
  onSubmit = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (values.responsibilityCenterRange === '1' && !values.responsibilityCenterIdList.length) {
          return message.warning('请至少选择一个责任中心！');
        }
        let params = {
          budgetStructure: {
            id: this.props.match.params.id,
            responsibilityCenterRequired: values.responsibilityCenterRequired,
            responsibilityCenterRange: values.responsibilityCenterRange,
            defaultResponsibilityCenterId:
              values.defaultResponsibilityCenterId && values.defaultResponsibilityCenterId.key,
            responsibilityVisible: values.responsibilityVisible,
          },
          responsibilityCenterIdList: values.responsibilityCenterIdList.map(item => item.id),
        };
        this.setState({ saveLoading: true });
        budgetService
          .saveResponsibilityCenter(params)
          .then(res => {
            message.success('保存成功！');
            this.setState({ saveLoading: false }, this.getList);
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
      }
    });
  };

  // 责任中心 取消
  saveCancel = () => {
    const { defaultListCache, setting } = this.state;
    this.props.form.resetFields();
    btnFlag = true;
    this.setState({
      defaultList:
        setting.budgetStructure.responsibilityCenterRange === '3'
          ? defaultListCache
          : setting.responsibilityCenterList,
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      infoList,
      dimension,
      updateState,
      structure,
      loading,
      showSlideFrameUpdate,
      data,
      columns,
      pagination,
      label,
      showSlideFrame,
      lov,
      defaultList,
      saveLoading,
      setting,
      spinning,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 3 },
      wrapperCol: { span: 10 },
    };
    const radioStyle = {
      display: 'block',
      marginBottom: '24px',
    };
    const selectorItem = {
      title: '自定义范围',
      url: `${config.mdataUrl}/api/responsibilityCenter/query/default?setOfBooksId=${
        this.props.match.params.setOfBooksId
      }`,
      searchForm: [
        {
          type: 'input',
          id: 'info',
          label: this.$t({ id: 'structure.responsibilityCenter' }) /*责任中心 */,
          colSpan: 8,
          placeholder: '请输入代码或名称',
        },
        {
          type: 'input',
          id: 'codeFrom',
          label: this.$t({ id: 'structure.responsibilityCenterFrom' }) /*责任中心代码从 */,
          colSpan: 8,
        },
        {
          type: 'input',
          id: 'codeTo',
          label: this.$t({ id: 'structure.responsibilityCenterTo' }) /*责任中心代码至 */,
          colSpan: 8,
        },
      ],
      columns: [
        {
          title: this.$t({ id: 'structure.responsibilityCenterCode' }) /*责任中心代码 */,
          dataIndex: 'responsibilityCenterCode',
          align: 'center',
        },
        {
          title: this.$t({ id: 'structure.responsibilityCenterName' }) /*责任中心名称 */,
          dataIndex: 'responsibilityCenterName',
          align: 'center',
        },
      ],
      key: 'id',
    };
    const budgetStructure = setting.budgetStructure;

    return (
      <div className="budget-structure-detail" style={{ paddingBottom: 20 }}>
        <BasicInfo
          infoList={infoList}
          infoData={structure}
          updateHandle={this.handleUpdate}
          updateState={updateState}
        />
        <div className="structure-detail-distribution">
          <Tabs onChange={this.onChangeTabs}>{this.renderTabs()}</Tabs>
        </div>
        {label === 'response' ? (
          <Spin size="large" spinning={spinning}>
            <Form onSubmit={this.onSubmit} style={{ marginTop: '26px' }}>
              <div className="common-item-title">是否必填</div>
              <FormItem {...formItemLayout} label=" " colon={false}>
                {getFieldDecorator('responsibilityCenterRequired', {
                  initialValue: budgetStructure
                    ? budgetStructure.responsibilityCenterRequired
                    : 'N',
                })(
                  <RadioGroup>
                    <Radio value="Y">是</Radio>
                    <Radio value="N" style={{ marginLeft: '30px' }}>
                      否
                    </Radio>
                  </RadioGroup>
                )}
              </FormItem>
              <div className="common-item-title">责任中心范围</div>
              <FormItem {...formItemLayout} label=" " colon={false}>
                {getFieldDecorator('responsibilityCenterRange', {
                  initialValue: budgetStructure ? budgetStructure.responsibilityCenterRange : '2',
                })(
                  <RadioGroup onChange={this.rangChange}>
                    <Radio value="2" style={radioStyle}>
                      部门对应责任中心
                    </Radio>
                    <Radio value="3" style={radioStyle}>
                      账套下所有责任中心
                    </Radio>
                    <Radio value="1" style={radioStyle}>
                      自定义范围
                    </Radio>
                  </RadioGroup>
                )}
              </FormItem>
              <FormItem {...formItemLayout} label="自定义范围">
                {getFieldDecorator('responsibilityCenterIdList', {
                  initialValue: setting.responsibilityCenterList,
                })(
                  <Chooser
                    disabled={
                      this.props.form.getFieldValue('responsibilityCenterRange') !== '1'
                        ? true
                        : false
                    }
                    placeholder="请选择责任中心"
                    selectorItem={selectorItem}
                    labelKey="responsibilityCenterName"
                    valueKey="id"
                    showDetail={false}
                    showNumber={true}
                    onChange={this.onResponsibilityChange}
                  />
                )}
              </FormItem>
              <FormItem {...formItemLayout} label="默认值">
                {getFieldDecorator('defaultResponsibilityCenterId', {
                  initialValue:
                    budgetStructure && budgetStructure.defaultResponsibilityCenterId
                      ? {
                          key: budgetStructure.defaultResponsibilityCenterId,
                          label: budgetStructure.defaultResponsibilityCenterName,
                        }
                      : undefined,
                })(
                  <Select
                    placeholder="请选择默认责任中心"
                    labelInValue
                    disabled={
                      this.props.form.getFieldValue('responsibilityCenterRange') == '2'
                        ? true
                        : false
                    }
                    getPopupContainer={node => node.parentNode}
                  >
                    {defaultList &&
                      defaultList.map(item => {
                        return <Option key={item.id}>{item.responsibilityCenterCodeName}</Option>;
                      })}
                  </Select>
                )}
              </FormItem>
              <div className="common-item-title">责任中心显示设置</div>
              <FormItem {...formItemLayout} label=" " colon={false}>
                {getFieldDecorator('responsibilityVisible', {
                  initialValue: budgetStructure
                    ? budgetStructure.responsibilityVisible
                    : 'editable',
                })(
                  <RadioGroup>
                    <Radio value="hidden">隐藏</Radio>
                    <Radio value="readOnly" style={{ marginLeft: '30px' }}>
                      只读
                    </Radio>
                    <Radio value="editable" style={{ marginLeft: '30px' }}>
                      可编辑
                    </Radio>
                  </RadioGroup>
                )}
              </FormItem>
              <div style={{ marginBottom: '20px' }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={saveLoading}
                  style={{ marginLeft: '40px' }}
                >
                  {this.$t({ id: 'common.save' })}
                </Button>
                <Button onClick={this.saveCancel} style={{ marginLeft: '40px' }} disabled={btnFlag}>
                  {this.$t({ id: 'common.cancel' })}
                </Button>
              </div>
            </Form>
          </Spin>
        ) : (
          <>
            <div className="table-header">
              <div className="table-header-title">
                {this.$t({ id: 'common.total' }, { total: `${pagination.total}` })}
              </div>{' '}
              {/*共搜索到*条数据*/}
              <div className="table-header-buttons">
                <Button
                  type="primary"
                  disabled={label === 'company' ? false : structure.usedFlag}
                  onClick={this.handleCreate}
                >
                  {label === 'company'
                    ? this.$t({ id: 'structure.addCompany' })
                    : this.$t({ id: 'common.create' })}
                </Button>{' '}
                {/*新建*/}
              </div>
            </div>
            <Table
              dataSource={data}
              columns={columns}
              loading={loading}
              onRow={record => ({
                onClick: () => this.handleRowClick(record),
              })}
              onChange={this.onChangePager}
              pagination={pagination}
              size="middle"
              bordered
            />
          </>
        )}

        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t({ id: 'common.back' })}
        </a>

        <SlideFrame
          title={this.$t({ id: 'structure.newDimension' })}
          show={showSlideFrame}
          onClose={() => this.showSlide(false)}
        >
          <NewDimension
            onClose={this.handleCloseSlide}
            params={{ ...structure, setOfBooksId: this.props.match.params.setOfBooksId }}
          />
        </SlideFrame>
        <SlideFrame
          title={this.$t({ id: 'structure.updateDimension' })}
          show={showSlideFrameUpdate}
          onClose={() => this.showSlideUpdate(false)}
        >
          <UpdateDimension
            onClose={this.handleCloseSlideUpdate}
            params={{
              ...dimension,
              setOfBooksId: this.props.match.params.setOfBooksId,
              flag: showSlideFrameUpdate,
            }}
          />
        </SlideFrame>

        <ListSelector
          type={lov.type}
          visible={lov.visible}
          extraParams={{ structureId: structure.id }}
          onOk={this.handleListOk}
          onCancel={() => this.showListSelector(false)}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    organization: state.budget.organization,
  };
}

const WrappedBudgetStructureDetail = Form.create({
  onValuesChange: () => (btnFlag = false),
})(BudgetStructureDetail);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedBudgetStructureDetail);
