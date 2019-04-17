/**
 * Created by 13576 on 2017/11/22.
 */
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import {
  Form,
  Spin,
  Timeline,
  Popover,
  Button,
  Collapse,
  Select,
  Affix,
  message,
  Popconfirm,
  notification,
  Icon,
  Badge,
  Row,
  Col,
  Input,
  Steps,
} from 'antd';
import Table from 'widget/table';
const Step = Steps.Step;
const FormItem = Form.Item;
import 'styles/budget/budget-journal-re-check/budget-journal-re-check-detail.scss';
import httpFetch from 'share/httpFetch';
import config from 'config';

import ApproveHistory from 'widget/Template/approve-history-work-flow';

import budgetJournalService from 'containers/approve/budget-journal-check/budget-journal-check.service';
import ApproveBar from 'components/Widget/Template/approve-bar';

class BudgetJournalCheckDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      passLoading: false,
      rejectLoading: false,
      spinLoading: false,
      data: [],
      journalCode: {},
      totalAmount: 0,
      params: {},
      headerAndListData: {},
      historyData: [],
      pageSize: 10,
      page: 0,
      total: 0,
      rowSelection: {
        type: 'checkbox',
        selectedRowKeys: [],
        onChange: this.onSelectChange,
        onSelect: this.onSelectItem,
        onSelectAll: this.onSelectAll,
      },
      organization: {},
      fileList: [],
      infoData: {},
      columns: [
        {
          /*公司*/
          title: this.$t('budgetJournal.companyId'),
          key: 'companyName',
          dataIndex: 'companyName',
          width: '8%',
          align: 'center',
          render: companyName => <Popover content={companyName}>{companyName}</Popover>,
        },
        {
          /*部门*/
          title: this.$t('budgetJournal.unitId'),
          key: 'departmentName',
          dataIndex: 'departmentName',
          width: '8%',
          align: 'center',
          render: unitName => <Popover content={unitName}>{unitName}</Popover>,
        },
        {
          /*责任中心 */
          title: '责任中心',
          align: 'center',
          key: 'responsibilityCenterCodeName',
          dataIndex: 'responsibilityCenterCodeName',
          width: '5%',
          render: responsibilityCenterCodeName => (
            <Popover content={responsibilityCenterCodeName}>{responsibilityCenterCodeName}</Popover>
          ),
        },
        {
          /*人员*/
          title: this.$t('budgetJournal.employeeId'),
          key: 'employeeName',
          align: 'center',
          dataIndex: 'employeeName',
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          /*预算项目*/
          title: this.$t('budgetJournal.item'),
          key: 'itemName',
          dataIndex: 'itemName',
          align: 'center',
          width: '16%',
          render: itemName => <Popover content={itemName}>{itemName}</Popover>,
        },
        {
          /*期间*/
          title: this.$t('budgetJournal.periodName'),
          key: 'periodName',
          dataIndex: 'periodName',
          align: 'center',
          width: '6%',
        },
        {
          /*季度*/
          title: this.$t('budgetJournal.periodQuarter'),
          width: '6%',
          key: 'periodQuarterName',
          align: 'center',
          dataIndex: 'periodQuarterName',
        },
        {
          /*年度*/
          title: this.$t('budgetJournal.periodYear'),
          key: 'periodYear',
          dataIndex: 'periodYear',
          align: 'center',
          width: '8%',
        },
        {
          /*币种*/
          title: this.$t('budgetJournal.currency'),
          key: 'currency',
          dataIndex: 'currency',
          align: 'center',
          width: '8%',
        },
        {
          /*汇率*/
          title: this.$t('budgetJournal.rate'),
          key: 'rate',
          dataIndex: 'rate',
          align: 'center',
          width: '8%',
        },
        {
          /*金额*/
          title: this.$t('budgetJournal.amount'),
          key: 'amount',
          dataIndex: 'amount',
          align: 'center',
          render: recode => (
            <Popover content={this.filterMoney(recode)}>{this.filterMoney(recode)}</Popover>
          ),
        },
        {
          /*本币今额*/
          title: this.$t('budgetJournal.functionalAmount'),
          key: 'functionalAmount',
          dataIndex: 'functionalAmount',
          align: 'center',
          render: recode => (
            <Popover content={this.filterMoney(recode)}>{this.filterMoney(recode)}</Popover>
          ),
        },
        {
          /*数字*/
          title: this.$t('budgetJournal.quantity'),
          key: 'quantity',
          dataIndex: 'quantity',
          align: 'center',
          with: '8%',
        },
        {
          /*备注*/
          title: this.$t('budgetJournal.remark'),
          key: 'remark',
          align: 'center',
          dataIndex: 'remark',
          render: remark => <Popover content={remark}>{remark}</Popover>,
        },
      ],
      //审批意见
      approvalTxt: '',
      headerData: {},
    };
  }

  componentDidMount = () => {
    this.getBudgetStructureInfo();
    this.getToleAmount();
    this.getBudgetJournalHead();
    this.getBudgetJournalLine();
  };

  /**
   * 获取预算表信息
   */
  getBudgetStructureInfo() {
    const journalCode = this.props.match.params.id;
    budgetJournalService
      .getBudgetStructureByJournalId(journalCode)
      .then(res => {
        console.log(res);
        if (res.data) {
          const columns = this.renderColumns(res.data);
          this.setState({
            columns,
          });
        }
      })
      .catch(err => {
        message.error(err.res.data.message);
        this.setState({ spinning: false });
      });
  }

  // 重渲染columns
  renderColumns = res => {
    let { columns } = this.state;
    let responsibilityVisible = String(res.responsibilityVisible).toLowerCase();
    let index = columns.findIndex(item => {
      return (index = item.key === 'responsibilityCenterCodeName');
    });
    if (responsibilityVisible === 'hidden') {
      columns.splice(index, 1);
    }
    return columns;
  };

  /**
   * 获取预算表信息
   */
  getBudgetStructureInfo() {
    const journalCode = this.props.match.params.id;
    budgetJournalService
      .getBudgetStructureByJournalId(journalCode)
      .then(res => {
        console.log(res);
        if (res.data) {
          const columns = this.renderColumns(res.data);
          this.setState({
            columns,
          });
        }
      })
      .catch(err => {
        message.error(err.res.data.message);
        this.setState({ spinning: false });
      });
  }

  // 重渲染columns
  renderColumns = res => {
    let { columns } = this.state;
    let responsibilityVisible = String(res.responsibilityVisible).toLowerCase();
    let index = columns.findIndex(item => {
      return (index = item.key === 'responsibilityCenterCodeName');
    });
    if (responsibilityVisible === 'hidden') {
      columns.splice(index, 1);
    }
    return columns;
  };

  //获取日记账总金额
  getToleAmount() {
    let infoDate = this.state.infoDate;
    budgetJournalService.getTotalCurrencyAmount(this.props.match.params.id).then(response => {
      let totalAmount = response.data;
      this.setState({ infoDate, totalAmount });
    });
  }

  //获取日志记账头行
  getHeadLine() {
    budgetJournalService.getBudgetJournalHeaderLine(this.props.match.params.id).then(resp => {
      this.setState(
        {
          loading: false,
          headerAndListData: resp.data,
          headerData: resp.data.dto,
          infoData: resp.data.dto,
          data: resp.data.list,
        },
        () => {
          this.getToleAmount();
        }
      );
    });
  }

  //获取日记账头
  getBudgetJournalHead() {
    const budgetCode = this.props.match.params.id;
    budgetJournalService.getBudgetJournalHeaderDetil(budgetCode).then(request => {
      this.getDimensionByStructureId(request.data.structureId);
      let headerData = request.data;
      if (headerData.attachmentOid != null) {
        headerData.attachmentOid.map(item => {
          this.getFileByAttachmentOid(item);
        });
      }
      let headerAndListData = {
        dto: request.data,
        list: [],
      };
      this.setState(
        {
          headerData: headerData,
          headerAndListData: headerAndListData,
          infoData: request.data,
        },
        () => {
          this.getToleAmount();
        }
      );
    });
  }

  //获取日记账行
  getBudgetJournalLine() {
    let params = {};
    params.page = this.state.page;
    params.size = this.state.pageSize;
    this.setState({
      loading: true,
      spinLoading: true,
    });

    const budgetCode = this.props.match.params.id;
    budgetJournalService
      .getBudgetJournalLineDetil(budgetCode, params)
      .then(response => {
        this.setState({
          loading: false,
          spinLoading: false,
        });
        let listData = response.data;
        this.setState({
          data: listData,
          total: Number(response.headers['x-total-count']),
          pagination: {
            total: Number(response.headers['x-total-count']),
            onChange: this.onChangePager,
            current: this.state.page + 1,
          },
        });
      })
      .catch(e => {
        this.setState({
          loading: false,
          spinLoading: false,
        });
        message.error(e.response.data.message);
      });
  }

  //分页点击
  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
          loading: true,
        },
        () => {
          this.getBudgetJournalLine();
        }
      );
  };

  //根据attachmentOid，查询附件
  getFileByAttachmentOid = value => {
    budgetJournalService
      .getFileByAttachmentOid(value)
      .then(resp => {
        let fileList = this.state.fileList;
        fileList.addIfNotExist(resp.data);
        this.setState({
          fileList: fileList,
        });
      })
      .catch(e => {
        message.error(`${this.$t('budgetJournal.getAttachmentFail')},${e.response.data.message}`);
      });
  };

  //根据预算表id，获得维度
  getDimensionByStructureId = value => {
    httpFetch
      .get(
        `${
          config.budgetUrl
        }/api/budget/journals/getLayoutsByStructureId?enabled=true&structureId=${value}`
      )
      .then(resp => {
        this.getColumnsAndDimensionhandleData(resp.data);
      })
      .catch(e => {
        message.error(`${this.$t('budgetJournal.getDimensionFail')},${e.response.data.message}`);
      });
  };

  //根据预算表的维度.获取维度Columuns
  getColumnsAndDimensionhandleData(dimensionList) {
    let columns = this.state.columns;
    for (let i = 0; i < dimensionList.length; i++) {
      const item = dimensionList[i];
      const priority = item.sequenceNumber;
      columns.push({
        title: `${item.dimensionName}`,
        key: `dimension${priority}ValueName`,
        dataIndex: `dimension${priority}ValueName`,
        render: recode => <Popover content={recode}>{recode}</Popover>,
      });
    }
    this.setState({
      columns,
    });
  }

  //通过
  handlePass = remark => {
    const dataValue = {
      approvalTxt: remark,
      entities: [
        {
          entityOid: this.state.headerAndListData.dto.documentOid,
          entityType: this.state.headerAndListData.dto.documentType,
        },
      ],
    };
    this.setState({
      spinLoading: true,
    });
    budgetJournalService
      .passJournalWorkflow(dataValue)
      .then(request => {
        message.success(this.$t('common.operate.success'));
        this.setState({
          spinLoading: false,
        });
        this.props.dispatch(
          routerRedux.push({
            pathname: `/approval-management/budget-journal-check`,
          })
        );
      })
      .catch(e => {
        message.error(`${this.$t('common.operate.filed')},${e.response.data.message}`);
        this.setState({
          spinLoading: false,
        });
      });
  };

  //驳回
  handleReject = remark => {
    const dataValue = {
      approvalTxt: remark,
      entities: [
        {
          entityOid: this.state.headerAndListData.dto.documentOid,
          entityType: this.state.headerAndListData.dto.documentType,
        },
      ],
    };
    this.setState({
      spinLoading: true,
    });
    httpFetch
      .post(`${config.baseUrl}/api/workflow/reject`, dataValue)
      .then(request => {
        message.success(this.$t('common.operate.success'));

        this.props.dispatch(
          routerRedux.push({
            pathname: `/approval-management/budget-journal-check`,
          })
        );
        this.setState({
          spinLoading: false,
        });
      })
      .catch(e => {
        message.error(`${this.$t('common.operate.filed')},${e.response.data.message}`);
        this.setState({
          spinLoading: false,
        });
      });
  };

  //返回列表页
  HandleReturn = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/approval-management/budget-journal-check`,
      })
    );
  };

  //返回状态
  getStatus = () => {
    const infoData = this.state.infoData;
    switch (infoData.status) {
      case 'NEW': {
        return <Badge status="processing" text={infoData.statusName} />;
      }
      case 'SUBMIT': {
        return <Badge status="warning" text={infoData.statusName} />;
      }
      case 'SUBMIT_RETURN': {
        return <Badge status="warning" text={infoData.statusName} />;
      }
      case 'REJECT': {
        return <Badge status="error" text={infoData.statusName} />;
      }
      case 'CHECKED': {
        return <Badge status="default" text={infoData.statusName} />;
      }
      case 'CHECKING': {
        return <Badge status="default" text={infoData.statusName} />;
      }
      case 'POSTED': {
        return <Badge status="default" text={infoData.statusName} />;
      }
      case 'BACKLASH_SUBMIT': {
        return <Badge status="default" text={infoData.statusName} />;
      }
      case 'BACKLASH_CHECKED': {
        return <Badge status="default" text={infoData.statusName} />;
      }
      default: {
        return <Badge status="default" text={infoData.statusName} />;
      }
    }
  };

  //获得总金额
  getAmount = () => {
    const data = this.state.totalAmount;
    return data;
  };

  //获取附件
  getFile = () => {
    const fileList = this.state.fileList;
    let file_arr = [];
    fileList.map(link => {
      file_arr.push(
        <div key={link.fileURL}>
          <a href={link.fileURL} target="_blank">
            <Icon type="paper-clip" /> {link.fileName}
          </a>{' '}
        </div>
      );
    });
    return file_arr.length > 0 ? file_arr : '-';
  };
  /**
   * 审批意见change事件
   */
  onApprovalTxtChange = e => {
    e.preventDefault();
    this.setState({
      approvalTxt: e.target.value,
    });
  };

  render() {
    const {
      data,
      columns,
      infoData,
      headerData,
      approvalTxt,
      passLoading,
      historyData,
      rejectLoading,
    } = this.state;
    const { getFieldDecorator } = this.props.form;

    return (
      <div style={{ paddingBottom: 100 }} className="budget-journal-re-check-detail">
        <Spin spinning={this.state.spinLoading}>
          <div className="base-info">
            <div className="base-info-header">{this.$t('budgetJournal.basicInformation')}</div>

            <Row className="base-info-cent">
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.status')}:</div>
                <div className="beep-info-text">{this.getStatus()}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.journalCode')}:</div>
                <div className="beep-info-text">
                  {infoData.journalCode ? infoData.journalCode : '-'}
                </div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.amount')}:</div>
                <div className="beep-info-text">{this.getAmount()}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.employeeId')}:</div>
                <div className="beep-info-text">
                  {infoData.employeeName ? infoData.employeeName : '-'}
                </div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.unitId')}:</div>
                <div className="beep-info-text">{infoData.unitName ? infoData.unitName : '-'}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.createdDate')}:</div>
                <div className="beep-info-text">
                  {String(infoData.createdDate).substring(0, 10)}
                </div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.journalTypeId')}:</div>
                <div className="beep-info-text">{infoData.journalTypeName}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.structureId')}:</div>
                <div className="beep-info-text">{infoData.structureName}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.scenarioId')}:</div>
                <div className="beep-info-text">{infoData.scenario}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.versionId')}:</div>
                <div className="beep-info-text">{infoData.versionName}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.periodStrategy')}:</div>
                <div className="beep-info-text">{infoData.periodStrategyName}</div>
              </Col>
              <Col span={8}>
                <div className="base-info-title">{this.$t('budgetJournal.attachment')}:</div>
                <div className="beep-info-text">{this.getFile()}</div>
              </Col>
            </Row>
          </div>

          <Table
            columns={columns}
            dataSource={data}
            bordered
            size="middle"
            scroll={{ x: '150%' }}
          />

          <div className="collapse">
            <ApproveHistory type="801002" oid={infoData.documentOid} formOid={true} />
          </div>
          {this.props.match.params.flag === 'approved' ? (
            <div className="bottom-bar bottom-bar-approve">
              <div style={{ lineHeight: '50px' }}>
                <Button
                  className="button-return"
                  style={{ marginLeft: '20px', marginRight: '8px' }}
                  onClick={this.HandleReturn}
                >
                  {this.$t('budgetJournal.return')}
                </Button>
              </div>
            </div>
          ) : (
            <div className="bottom-bar bottom-bar-approve">
              <ApproveBar
                style={{ paddingLeft: 20 }}
                passLoading={passLoading}
                backUrl={'/approval-management/budget-journal-check/budget-journal-check'}
                documentType={801002}
                documentOid={headerData.documentOid}
              />
            </div>
            /*   <Affix offsetBottom={0} className="bottom-bar">
                    <div>
                      <Row gutter={12} type='flex' justify='start'>
                        <Col offset={1}><span>{this.$t('budgetJournal.budgetOpinion')}：&nbsp;</span></Col>
                        <Col span={11} >
                          <Input required='true' value={approvalTxt} onChange={this.onApprovalTxtChange} placeholder={this.$t('common.please.enter')} />
                        </Col>
                        <Col span={1.5} ><Button type="primary" onClick={this.handlePass} loading={this.state.passLoading}>{this.$t('budgetJournal.pass')}</Button></Col>
                        <Col span={1.5} ><Button type="danger" style={{ background: 'red', color: 'white' }} loading={this.state.rejectLoading} onClick={this.handleReject}>{this.$t('budgetJournal.reject')}</Button></Col>
                        <Col span={2} offset={2} ><Button onClick={this.HandleReturn}>{this.$t('budgetJournal.return')}</Button></Col>
                      </Row>
                    </div>
                  </Affix>*/
          )}
        </Spin>
      </div>
    );
  }
}

const WebBudgetJournalCheckDetail = Form.create()(BudgetJournalCheckDetail);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
    organization: state.user.organization,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WebBudgetJournalCheckDetail);