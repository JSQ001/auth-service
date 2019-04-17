import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, message, Popover, Badge, Popconfirm, Icon, Alert } from 'antd';
import Table from 'widget/table';
import BasicInfo from 'widget/basic-info';
import { messages } from 'utils/utils';
import service from './service';
import NewRuleDetail from './new-detail';
import SlideFrame from 'widget/slide-frame';
import moment from 'moment';

class RuleDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      data: [],
      searchParams: {},
      page: 0,
      buttonLoading: false,
      pageSize: 10,
      columns: [
        {
          title: messages('workbench.businessType.dispatch.rule.priority' /*优先级*/),
          dataIndex: 'priority',
          width: 60,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.andOr' /*逻辑操作*/),
          dataIndex: 'andOr',
          width: 50,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.leftBracket' /*左括号*/),
          dataIndex: 'leftBracket',
          width: 50,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.parameter' /*规则参数*/),
          dataIndex: 'parameterName',
          width: 100,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.judgeRule' /*条件类型*/),
          dataIndex: 'judgeRuleName',
          width: 80,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.judgeData' /*值*/),
          dataIndex: 'judgeData',
          width: 80,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.rightBracket' /*右括号*/),
          dataIndex: 'rightBracket',
          width: 50,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.sqlParam1' /*sql参数1*/),
          dataIndex: 'sqlParam1',
          width: 60,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.sqlParam2' /*sql参数2*/),
          dataIndex: 'sqlParam2',
          width: 60,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.sqlParam3' /*sql参数3*/),
          dataIndex: 'sqlParam3',
          width: 60,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.sqlParam4' /*sql参数4*/),
          dataIndex: 'sqlParam4',
          width: 60,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.dispatch.rule.sqlParam5' /*sql参数5*/),
          dataIndex: 'sqlParam5',
          width: 60,
          render: value => {
            return <Popover content={value}>{value}</Popover>;
          },
        },
        {
          title: messages('workbench.businessType.enabled') /*状态*/,
          dataIndex: 'enabled',
          align: 'center',
          width: 60,
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
          align: 'center',
          width: 104,
          render: (text, record) => (
            <span>
              <a onClick={() => this.handleEdit(record)}>{this.$t('workbench.edit')}</a>
              {/*编辑*/}
              <span className="ant-divider" />
              <Popconfirm
                key={record.id + 'delete'}
                title={messages('common.confirm.delete' /*确定要删除吗？*/)}
                onConfirm={() => this.handleDelete(record.id)}
              >
                <a>{this.$t('workbench.delete')}</a>
                {/*删除*/}
              </Popconfirm>
            </span>
          ),
        },
      ],
      infoList: [
        {
          type: 'input',
          id: 'ruleCode',
          colSpan: 5,
          disabled: true,
          isRequired: true,
          label: messages('workbench.businessType.dispatch.rule.code' /*派工规则代码*/),
        },
        {
          type: 'input',
          id: 'ruleName',
          colSpan: 5,
          isRequired: true,
          label: messages('workbench.businessType.dispatch.rule.name' /*派工规则名称*/),
        },
        {
          type: 'items',
          id: 'date',
          infoLabel: messages('workbench.businessType.dispatch.rule.date' /*有效日期*/) + ':',
          items: [
            {
              type: 'date',
              colSpan: 5,
              isRequired: true,
              id: 'startDate',
              label: messages('workbench.businessType.dispatch.rule.start' /*有效日期从*/),
            },
            {
              type: 'date',
              colSpan: 5,
              id: 'endDate',
              label: messages('workbench.businessType.dispatch.rule.end' /*有效日期至*/),
            },
          ],
        },
        {
          type: 'switch',
          id: 'enabled',
          defaultValue: false,
          colSpan: 4,
          label: messages('workbench.businessType.enabled') /*状态*/,
        },
      ],
      pagination: {
        total: 0,
      },
      editorRecord: [],
      businessTypeId: this.props.match.params.businessTypeId,
      businessTypeDetailUrl: '/workbench/business-type/business-type/detail/:id/:tab',
      updateState: false,
      frameTitle: '',
      frameFlag: false,
      detailRecord: {},
      isEditor: false,
    };
  }

  componentWillMount() {
    this.getRule(this.props.match.params.id);
    this.getList();
  }

  getRule = id => {
    this.setState({ buttonLoading: true });
    service
      .getRuleById(id)
      .then(res => {
        let data = res.data;
        data['date'] =
          moment(data.startDate).format('YYYY-MM-DD') +
          ' ~ ' +
          (data.endDate ? moment(data.endDate).format('YYYY-MM-DD') : '');
        this.setState({ buttonLoading: false, editorRecord: data });
      })
      .catch(e => {
        message.error(
          messages('common.error') /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/
        );
        this.setState({ buttonLoading: false });
      });
  };
  getList = () => {
    this.setState({ loading: true });
    const { page, pageSize } = this.state;
    service
      .queryRuleDetail(page, pageSize, this.props.match.params.id)
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

  // 新建
  createRuleDetail = () => {
    this.setState({
      frameFlag: true,
      detailRecord: {},
      frameTitle: this.$t('workbench.the.new.subsidiary.rules') /*新增明细规则*/,
      isEditor: false,
    });
  };
  // 编辑
  handleEdit = record => {
    this.setState({
      frameFlag: true,
      detailRecord: record,
      frameTitle: this.$t('workbench.edit.detailed.rules') /*编辑明细规则*/,
      isEditor: true,
    });
  };
  // 删除
  handleDelete = id => {
    service
      .deleteRuleDetail(id)
      .then(res => {
        message.success(messages('common.operate.success')); //操作成功
        this.getList();
      })
      .catch(e => {
        if (e.response) {
          message.error(
            messages('common.operate.filed' /*操作失败*/) + '!' + e.response.data.message
          );
        } else {
          message.error(messages('common.operate.filed'));
        }
      });
  };
  // 返回
  backFunction = () => {
    const { businessTypeId, businessTypeDetailUrl } = this.state;
    let path = businessTypeDetailUrl.replace(':id', businessTypeId).replace(':tab', 'RULE');
    this.props.dispatch(
      routerRedux.push({
        pathname: path,
        query: { id: businessTypeId },
      })
    );
  };

  updateHandle = values => {
    let { editorRecord } = this.state;
    if (values.endDate && values.endDate < values.startDate) {
      message.error(this.$t('workbench.desc.code1')); // 有效期至必须大于有效期从！
      return;
    }
    editorRecord = { ...editorRecord, ...values };
    this.setState({
      buttonLoading: true,
    });
    service
      .updateRule(editorRecord)
      .then(res => {
        let data = res.data;
        data['date'] =
          moment(data.startDate).format('YYYY-MM-DD') +
          ' ~ ' +
          (data.endDate ? moment(data.endDate).format('YYYY-MM-DD') : '');
        this.setState({ buttonLoading: false, updateState: true, editorRecord: data });
        message.success(messages('common.operate.success')); //保存成功
      })
      .catch(e => {
        if (e.response) {
          message.error(messages('common.save.filed' /*保存失败*/) + '!' + e.response.data.message);
        } else {
          message.error(messages('common.operate.filed'));
        }
        this.setState({ buttonLoading: false });
      });
  };
  //侧滑窗口关闭
  cancelWindow = flag => {
    this.setState({ frameFlag: false }, () => {
      if (flag === true) {
        this.getList();
      }
    });
  };

  render() {
    const {
      businessTypeId,
      columns,
      data,
      loading,
      infoList,
      buttonLoading,
      pagination,
      editorRecord,
      updateState,
      frameTitle,
      frameFlag,
      detailRecord,
      isEditor,
    } = this.state;

    return (
      <div className="header-title">
        <BasicInfo
          infoList={infoList}
          colSpan={6}
          infoData={editorRecord}
          updateHandle={this.updateHandle}
          updateState={updateState}
          loading={buttonLoading}
        />
        <Alert
          style={{ marginTop: '16px' }}
          message={messages('workbench.businessType.dispatch.rule.messageInfo')}
          type="info"
          showIcon
        />
        <div className="table-header">
          <div className="table-header-buttons">
            <Button disabled={editorRecord.enabled} type="primary" onClick={this.createRuleDetail}>
              {this.$t({ id: 'common.create' } /*新建*/)}
            </Button>
          </div>
        </div>
        <Table
          rowKey={record => record.id}
          columns={
            editorRecord.enabled ? columns.filter(({ key }) => key !== 'operation') : columns
          }
          dataSource={data}
          loading={loading}
          pagination={pagination}
          bordered
          size="middle"
        />
        <SlideFrame title={frameTitle} show={frameFlag} onClose={e => this.cancelWindow(e)}>
          <NewRuleDetail
            onClose={e => this.cancelWindow(e)}
            params={{
              record: detailRecord,
              isEditor: isEditor,
              ruleId: editorRecord.id,
              businessTypeId: businessTypeId,
            }}
          />
        </SlideFrame>
        <a className="back" onClick={this.backFunction}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t('workbench.return')}
          {/*返回*/}
        </a>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(RuleDetail);
