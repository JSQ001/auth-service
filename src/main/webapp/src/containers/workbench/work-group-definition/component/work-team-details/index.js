import React, { Component } from 'react';
import { Input, Row, Col, Switch, Button, message } from 'antd';
import Chooser from 'widget/chooser';
import service from '../../work-group-definition-service';

class WorkTeamDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      workDetails: JSON.parse(JSON.stringify(this.props.workDetails)), //右侧工作组信息
      _workDetails: { ...this.props.workDetails }, //副本
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      workDetails: JSON.parse(JSON.stringify(nextProps.workDetails)),
      _workDetails: { ...nextProps.workDetails },
    });
  }

  //改变状态-toEdit编辑 'edit' / 'normal'
  handleToEditStatus = e => {
    e.preventDefault();
    this.props.changeStatus('edit');
  };

  //获取工作组信息编辑后的值
  getEditValue = (value, dataIndex) => {
    const { _workDetails, workDetails } = this.state;

    switch (dataIndex) {
      case 'enabled':
        workDetails.enabled = value;
        break;
      case 'workManager':
        workDetails.workManager = value[0].userName;
        workDetails.workManagerId = value[0].userId;
        break;
      default:
        workDetails[dataIndex] = value;
    }
    this.setState({ _workDetails, workDetails });
  };

  //保存
  handleClickToEdit = () => {
    const { workDetails } = this.state;

    service
      .editWorkTeamValue(workDetails)
      .then(res => {
        message.success(this.$t('common.update.success'));
        this.props.changeStatus('normal');
        this.props.reRenderTree(res.data);
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //取消编辑
  handleClickToCancel = () => {
    this.setState({
      workDetails: { ...this.state._workDetails },
    });
    this.props.changeStatus('normal');
  };

  render() {
    const { workDetails } = this.state;
    const status = this.props.status;
    return (
      <div className="work-team-details">
        <p style={{ textAlign: 'right', padding: '10px', margin: '0' }}>
          {status == 'edit' ? (
            <span />
          ) : (
            <a onClick={e => this.handleToEditStatus(e)}>
              {/* 编辑 */}
              {this.$t('common.edit')}
            </a>
          )}
        </p>
        <div>
          <Row style={{ marginBottom: '5px', lineHeight: '32px' }}>
            <Col span={12}>
              <Row>
                <Col span={8} className="details-name-style">
                  {this.$t('workbench.workTeam.teamCode')}
                </Col>
                <Col span={16}>
                  {status === 'normal' ? (
                    <span style={{ padding: '0 10px' }}>
                      {workDetails.workTeamCode ? workDetails.workTeamCode : '-'}
                    </span>
                  ) : (
                    <Input
                      style={{ width: '90%' }}
                      disabled={status === 'edit' ? true : false}
                      defaultValue={workDetails.workTeamCode}
                      onChange={e => this.getEditValue(e.target.value, 'workTeamCode')}
                    />
                  )}
                </Col>
              </Row>
            </Col>
            <Col span={12}>
              <Row>
                <Col span={8} className="details-name-style">
                  {this.$t('workbench.workTeam.teamName')}
                </Col>
                <Col span={16}>
                  {status === 'normal' ? (
                    <span style={{ padding: '0 10px' }}>
                      {workDetails.workTeamName ? workDetails.workTeamName : '-'}
                    </span>
                  ) : (
                    <Input
                      style={{ width: '90%' }}
                      defaultValue={workDetails.workTeamName}
                      onChange={e => this.getEditValue(e.target.value, 'workTeamName')}
                    />
                  )}
                </Col>
              </Row>
            </Col>
          </Row>
          <Row style={{ lineHeight: '32px' }}>
            <Col span={12}>
              <Row>
                <Col span={8} className="details-name-style">
                  {this.$t('workbench.workTeam.leader')}
                </Col>
                <Col span={16}>
                  {status === 'normal' ? (
                    <span style={{ padding: '0 10px' }}>
                      {workDetails.workManager ? workDetails.workManager : '-'}
                    </span>
                  ) : (
                    <div style={{ width: '90%' }}>
                      <Chooser
                        selectorItem={this.props.selectorItem}
                        labelKey="userName"
                        valueKey="userId"
                        single={true}
                        value={[
                          {
                            userName: workDetails.workManager || '',
                            userId: workDetails.workManagerId || '',
                          },
                        ]}
                        onChange={value => this.getEditValue(value, 'workManager')}
                        method={this.props.selectorItem.method}
                      />
                    </div>
                  )}
                </Col>
              </Row>
            </Col>
            <Col span={12}>
              <Row>
                <Col span={8} className="details-name-style">
                  {this.$t('workbench.workTeam.status')}
                </Col>
                <Col span={16}>
                  {status === 'normal' ? (
                    <span style={{ padding: '0 10px' }}>
                      {workDetails.enabled == true
                        ? this.$t('common.enabled')
                        : this.$t('common.disabled')}
                    </span>
                  ) : (
                    <div>
                      <Switch
                        checked={workDetails.enabled}
                        onChange={value => this.getEditValue(value, 'enabled')}
                      />
                      <span style={{ paddingLeft: '10px' }}>
                        {workDetails.enabled == true
                          ? this.$t('common.enabled')
                          : this.$t('common.disabled')}
                      </span>
                    </div>
                  )}
                </Col>
              </Row>
            </Col>
          </Row>
          {status === 'edit' ? (
            <div style={{ textAlign: 'right', padding: '0 10px' }}>
              <Button
                type="primary"
                style={{ marginRight: '10px' }}
                onClick={this.handleClickToEdit}
              >
                {this.$t('request.detail.loan.confirm')}
              </Button>
              <Button onClick={this.handleClickToCancel}>{this.$t('common.cancel')}</Button>
            </div>
          ) : (
            <div />
          )}
        </div>
      </div>
    );
  }
}

export default WorkTeamDetails;
