import React, { Component } from 'react';
import CustomTable from 'widget/custom-table';
import { connect } from 'dva';
import config from 'config';
import moment from 'moment';
import { message, Popover, Modal, Popconfirm } from 'antd';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';
import detailsService from './details-service';

class ContractInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: this.$t('acp.contract.number'), // 合同编号
          dataIndex: 'contractNumber',
          align: 'center',
          render: (text, record) => {
            return (
              <Popover content={text}>
                <a
                  onClick={e => {
                    this.handleContractShow(e, record);
                  }}
                >
                  {text}
                </a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('acp.contract.name'), // 合同名称
          dataIndex: 'contractName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('my.contract.contractCompany'), // 合同公司
          dataIndex: 'companyName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('my.contract.signDate'), // 签署日期
          dataIndex: 'signDate',
          align: 'center',
          render: data => {
            return (
              <Popover content={moment(data).format('YYYY-MM-DD')}>
                {moment(data).format('YYYY-MM-DD')}
              </Popover>
            );
          },
        },
        {
          title: this.$t('my.contract.part'),
          dataIndex: 'partnerCategoryName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('acp.currency'), // 币种
          dataIndex: 'currency',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t({ id: 'my.contract.amount' }), // 合同金额
          dataIndex: 'amount',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          render: (text, record) => {
            return (
              <Popconfirm
                title="你确定要删除?"
                onConfirm={() => {
                  this.handleDelete(record);
                }}
              >
                <a>{this.$t('common.delete')}</a>
              </Popconfirm>
            );
          },
        },
      ],
      columns2: [
        {
          title: this.$t('acp.contract.number'), // 合同编号
          dataIndex: 'contractNumber',
          align: 'center',
          render: (text, record) => {
            return (
              <Popover content={text}>
                <a
                  onClick={e => {
                    this.handleContractShow(e, record);
                  }}
                >
                  {text}
                </a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('acp.contract.name'), // 合同名称
          dataIndex: 'contractName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('my.contract.contractCompany'), // 合同公司
          dataIndex: 'companyName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('my.contract.signDate'), // 签署日期
          dataIndex: 'signDate',
          align: 'center',
          render: data => {
            return (
              <Popover content={moment(data).format('YYYY-MM-DD')}>
                {moment(data).format('YYYY-MM-DD')}
              </Popover>
            );
          },
        },
        {
          title: this.$t('my.contract.part'),
          dataIndex: 'partnerCategoryName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('acp.currency'), // 币种
          dataIndex: 'currency',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t({ id: 'my.contract.amount' }), // 合同金额
          dataIndex: 'amount',
          align: 'center',
          tooltips: true,
        },
      ],
      contractShow: false,
      contractId: '',
    };
  }

  // 行删除
  handleDelete = record => {
    // e.preventDefault();
    const { id } = this.props;
    const params = {
      proReqId: id,
      contractHeaderId: record.id,
    };
    detailsService
      .deleteContractValue(params)
      .then(res => {
        if (res) {
          message.success(this.$t('common.delete.success'));
          this.table.search();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 查看
  handleContractShow = (e, record) => {
    e.preventDefault();
    this.setState({
      contractId: record.id,
      contractShow: true,
    });
  };

  // 搜索重渲染行数据
  search = () => {
    this.table.search();
  };

  render() {
    const { columns, columns2, contractShow, contractId } = this.state;
    const { id } = this.props;
    return (
      <div>
        <CustomTable
          columns={this.props.flags == 1 ? columns : columns2}
          ref={ref => {
            this.table = ref;
          }}
          showNumber
          url={`${config.contractUrl}/api/project/requisition/contract/query/by/pro/req/id/${id}`}
        />
        {/* 合同详情 */}
        <Modal
          title="合同详情"
          visible={contractShow}
          onCancel={() => {
            this.setState({ contractShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <ContractDetail id={contractId} isApprovePage />
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ContractInfo);
