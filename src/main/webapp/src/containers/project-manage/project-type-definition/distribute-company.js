import React from 'react';
import { Form, Button, Checkbox, Icon, Row, Col, Badge, message } from 'antd';
import ListSelector from 'widget/list-selector';
import CustomTable from 'widget/custom-table';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import config from 'config';
import service from './servers';

class ProjectDistributeCompany extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      infoConfig: [
        { dataIndex: 'setOfBooksName', label: this.$t('chooser.data.setOfBooks') /* 账套 */ },
        {
          dataIndex: 'projectReqTypeCode',
          label: this.$t('contract.project.type.code') /* 项目申请单类型代码 */,
        },
        {
          dataIndex: 'projectReqTypeName',
          label: this.$t('contract.project.type.name') /* 项目申请单类型名称 */,
        },
        { dataIndex: 'enabled', label: this.$t('common.column.status') /* 状态 */, type: 'switch' },
      ],
      columns: [
        {
          title: this.$t('chooser.data.companyCode') /* 公司代码 */,
          dataIndex: 'companyCode',
          align: 'center',
        },
        {
          title: this.$t('chooser.data.companyName') /* 公司名称 */,
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: this.$t('chooser.data.companyType') /* 公司类型 */,
          dataIndex: 'companyType',
          align: 'center',
        },
        {
          title: this.$t('common.enabled') /* 启用 */,
          dataIndex: 'enabled',
          width: 120,
          align: 'center',
          render: (value, record) => (
            <Checkbox checked={value} onChange={() => this.handleStatusChange(record)} />
          ),
        },
      ],
      showListSelector: false,
      selectorItem: {
        title: this.$t('budget.item.batchCompany') /* 批量分配公司 */,
        url: `${config.contractUrl}/api/project/requisition/type/company/${
          props.match.params.id
        }/query/filter?setOfBooksId=${props.match.params.setOfBooksId}`,
        searchForm: [
          {
            type: 'input',
            id: 'companyCode',
            label: this.$t('chooser.data.companyCode') /* 公司代码 */,
          },
          {
            type: 'input',
            id: 'companyName',
            label: this.$t('chooser.data.companyName') /* 公司名称 */,
          },
          {
            type: 'input',
            id: 'companyCodeFrom',
            label: this.$t('chooser.data.companyCode.from') /* 公司代码从 */,
          },
          {
            type: 'input',
            id: 'companyCodeTo',
            label: this.$t('chooser.data.companyCode.to') /* 公司代码至 */,
          },
        ],
        columns: [
          { title: this.$t('chooser.data.companyCode') /* 公司代码 */, dataIndex: 'companyCode' },
          { title: this.$t('chooser.data.companyName') /* 公司名称 */, dataIndex: 'name' },
          {
            title: this.$t('chooser.data.companyType') /* 公司类型 */,
            dataIndex: 'companyTypeName',
          },
        ],
        key: 'id',
      },
      infoList: {},
    };
  }

  componentDidMount() {
    this.getBaseInfo();
  }

  // 获取基本信息
  getBaseInfo = () => {
    const {
      match: { params },
    } = this.props;
    service
      .getInfoData(params.id)
      .then(res => {
        this.setState({ infoList: res.data });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 状态 切换
  handleStatusChange = record => {
    service
      .editStatus(record)
      .then(() => {
        this.table.search();
        message.success(this.$t('common.operate.success'));
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 弹框显示，隐藏
  handleListShow = flag => {
    this.setState({ showListSelector: flag });
  };

  // 弹出框 确定
  handleListOk = value => {
    const { result } = value;
    const {
      match: { params },
    } = this.props;
    if (result.length) {
      const data = result.map(item => item.id);
      this.handleListShow(false);
      service
        .distributeCompany(data, params.id)
        .then(() => {
          message.success(this.$t('common.operate.success'));
          this.table.search();
        })
        .catch(err => message.error(err.response.data.message));
    } else {
      message.warning(this.$t('contract.project.type.company.please'));
    }
  };

  // 返回
  handleBack = () => {
    const {
      dispatch,
      match: { params },
    } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/document-type-manage/project-type-definition/project-type-definition/${
          params.setOfBooksId
        }`,
      })
    );
  };

  // 生成列 的值
  renderItem = item => {
    const { infoList } = this.state;
    const value = infoList[item.dataIndex];
    switch (item.type) {
      case 'switch':
        return (
          <Badge
            status={value ? 'success' : 'error'}
            text={value ? this.$t('common.status.enable') : this.$t('common.status.disable')}
          />
        );
      default:
        return value;
    }
  };

  render() {
    const { columns, showListSelector, selectorItem, infoConfig } = this.state;
    const {
      match: { params },
    } = this.props;
    return (
      <div className="company-distribution">
        <Row
          style={{ background: '#f7f7f7', padding: '20px 25px 0', borderRadius: '6px 6px 0 0' }}
          key="1"
        >
          {infoConfig.map(item => {
            return (
              <Col span={6} style={{ marginBottom: '15px' }} key={item.dataIndex}>
                <div style={{ color: '#989898' }}>{item.label}</div>
                <div style={{ wordWrap: 'break-word' }}>{this.renderItem(item)}</div>
              </Col>
            );
          })}
        </Row>

        <div style={{ margin: '15px 0' }}>
          <Button type="primary" onClick={() => this.handleListShow(true)}>
            {this.$t('budget.item.batchCompany') /* 批量分配公司 */}
          </Button>
        </div>

        <CustomTable
          columns={columns}
          url={`${
            config.contractUrl
          }/api/project/requisition/type/company/pageAssignCompany?requisitionTypeId=${params.id}`}
          ref={ref => {
            this.table = ref;
          }}
        />

        {/* 分配公司 */}
        <ListSelector
          visible={showListSelector}
          onCancel={() => this.handleListShow(false)}
          selectorItem={selectorItem}
          onOk={this.handleListOk}
        />

        <div style={{ padding: '15px 0' }}>
          <a onClick={this.handleBack}>
            <Icon type="rollback" style={{ marginRight: '5px' }} />
            {this.$t('budgetJournal.return')}
          </a>
        </div>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(ProjectDistributeCompany);

export default connect()(wrappedCompanyDistribution);
