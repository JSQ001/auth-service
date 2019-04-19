import React, { Component } from 'react';
import { Affix, Row, Button, message, Popconfirm } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import PropTypes from 'prop-types';
import detailsService from './details-service';

class BottomBar extends Component {
  constructor(props) {
    super(props);
    this.state = {
      operationLoading: false,
    };
  }

  // 提交
  onSubmit = () => {
    const { headerInfo } = this.props;
    this.setState({
      operationLoading: true,
    });
    const params = {
      documentId: headerInfo.id,
    };
    detailsService
      .submitProject(params)
      .then(res => {
        if (res) {
          message.success(this.$t('pay.backlash.submitSuccess'));
          this.setState({ operationLoading: false }, () => {
            // const { refresh } = this.props;
            // refresh();
            this.onBack();
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ operationLoading: false });
      });
  };

  // 返回
  onBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: '/project-manage/my-project-apply/my-project-apply',
      })
    );
  };

  // 删除
  onDelete = () => {
    const { headerInfo } = this.props;
    detailsService
      .deleteProject(headerInfo.id)
      .then(res => {
        if (res) {
          message.success(this.$t('删除成功'));
          this.onBack();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  render() {
    const { headerInfo, onlyReturn } = this.props;
    const { operationLoading } = this.state;
    return (
      <Affix
        offsetBottom={0}
        className="bottom-bar bottom-bar-approve"
        style={{
          position: 'fixed',
          bottom: 0,
          width: '100%',
          height: '50px',
          boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
          background: '#fff',
          lineHeight: '50px',
          zIndex: 1,
        }}
      >
        {Number(headerInfo.statusCode) === 1001 ||
        Number(headerInfo.statusCode) === 1005 ||
        Number(headerInfo.statusCode) === 1003 ||
        onlyReturn ? (
          <Row style={{ marginLeft: '30px' }}>
            <Button
              style={{ marginLeft: '20px' }}
              loading={operationLoading}
              onClick={this.onSubmit}
              type="primary"
            >
              {/* 提交 */}
              {this.$t('common.submit')}
            </Button>

            <Popconfirm title="确定删除?" onConfirm={this.onDelete}>
              <Button style={{ marginLeft: '20px' }}>{this.$t('common.delete')}</Button>
            </Popconfirm>

            <Button style={{ marginLeft: '20px' }} onClick={this.onBack}>
              {this.$t('budgetJournal.return')}
            </Button>
          </Row>
        ) : (
          <Row style={{ marginLeft: '30px' }}>
            <Button onClick={this.onBack}>{this.$t('budgetJournal.return')}</Button>
          </Row>
        )}
      </Affix>
    );
  }
}

BottomBar.propTypes = {
  headerInfo: PropTypes.object,
  onlyReturn: PropTypes.bool,
};

BottomBar.defaultProps = {
  headerInfo: {},
  onlyReturn: false, // 是否只显示返回按钮
};

export default connect()(BottomBar);
