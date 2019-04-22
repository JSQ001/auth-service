import React from 'react';
import { connect } from 'dva';
import { message } from 'antd';
import { routerRedux } from 'dva/router';
import service from './service';
import { messages } from 'utils/utils';
import SearchArea from 'widget/search-area';

class NewRule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      businessTypeId: this.props.match.params.businessTypeId,
      businessTypeDetailUrl: '/workbench/business-type/business-type/detail/:id/:tab',
      searchForm: [
        {
          type: 'input',
          id: 'ruleCode',
          colSpan: 5,
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
        {
          type: 'switch',
          id: 'enabled',
          disabled: true,
          defaultValue: false,
          colSpan: 4,
          label: messages('workbench.businessType.enabled') /*状态*/,
        },
      ],
      detailUrl: '/workbench/business-type/dispatch/rule/detail/:businessTypeId/:id',
    };
  }

  componentDidMount() {}
  // 保存
  saveHandle = values => {
    if (values.endDate && values.endDate < values.startDate) {
      message.error(this.$t('workbench.desc.code1')); // 有效期至必须大于有效期从！
      return;
    }
    this.setState({ loading: true });
    values.businessTypeId = this.state.businessTypeId;
    service
      .createRule(values)
      .then(res => {
        message.success(messages('common.operate.success')); //保存成功
        this.setState({ loading: false });

        const { businessTypeId, detailUrl } = this.state;
        let path = detailUrl.replace(':businessTypeId', businessTypeId).replace(':id', res.data.id);
        this.props.dispatch(
          routerRedux.push({
            pathname: path,
          })
        );
      })
      .catch(e => {
        if (e.response) {
          message.error(messages('common.save.filed' /*保存失败*/) + '!' + e.response.data.message);
        } else {
          message.error(messages('common.operate.filed'));
        }
        this.setState({ loading: false });
      });
  };

  // 取消
  cancelHandle = () => {
    const { businessTypeId, businessTypeDetailUrl } = this.state;
    let path = businessTypeDetailUrl.replace(':id', businessTypeId).replace(':tab', 'RULE');
    this.props.dispatch(
      routerRedux.push({
        pathname: path,
        query: { id: businessTypeId },
      })
    );
  };
  render() {
    const { searchForm, loading } = this.state;
    return (
      <div className="header-title">
        <SearchArea
          maxLength={5}
          okText={this.$t('workbench.save')} /*保存*/
          clearText={this.$t('workbench.cancel')} /*取消*/
          loading={loading}
          submitHandle={this.saveHandle}
          clearHandle={this.cancelHandle}
          searchForm={searchForm}
        />
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
)(NewRule);
