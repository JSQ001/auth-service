import React, { Component } from 'react';
import { Icon } from 'antd';
import 'styles/workbench/work-group-definition/search-list-tree.scss';

class SearchListTree extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchValue: props.searchValue, //查询内容在查询结果中用不同颜色体现
      teamData: props.teamData,
      userData: props.userData,
    };
  }

  componentDidMount = () => {};

  componentWillReceiveProps = nextProps => {
    this.setState({
      searchValue: nextProps.searchValue,
      teamData: nextProps.teamData,
      userData: nextProps.userData,
    });
  };

  //渲染员工
  renderUserList = userData => {
    if (userData.length < 1) {
      if (this.props.isSearching) {
        return (
          <div className="no-user">
            <Icon type="frown" />
            {/*正在搜索*/
            this.$t('sdp.is-searching')}
          </div>
        );
      } else
        return (
          <div className="no-user">
            <Icon type="frown" />
            <span style={{ padding: '0 5px' }}>
              {/* 没有搜索到员工 */}
              {this.$t('workbench.workTeam.employee.selected.nothing')}
            </span>
          </div>
        );
    }
    return userData.map((user, index) => {
      return (
        <div
          className={user.active ? 'user-title title-selected-active' : 'user-title'}
          onClick={() => {
            this.selectedValueAfterSearch(user, index, 'user');
          }}
          key={`${user.workTeamId}-${user.userId}-${index}`}
        >
          <Icon type="user" />
          {this.renderColorText(user.userDisplay)}
        </div>
      );
    });
  };

  //渲染工作组
  renderTeamList = teamData => {
    if (teamData.length < 1) {
      if (this.props.isSearching) {
        return (
          <div className="no-person">
            <Icon type="frown" />
            {/*正在搜索*/
            this.$t('sdp.is-searching')}
          </div>
        );
      }
      return (
        <div className="no-work-team">
          <Icon type="frown" />
          <span style={{ padding: '0 5px' }}>
            {/* 没有搜索到工作组 */}
            {this.$t('workbench.workTeam.team.nothing')}
          </span>
        </div>
      );
    }
    return teamData.map((workTeam, index) => {
      return (
        <div
          className={workTeam.active ? 'team-title title-selected-active' : 'team-title'}
          style={{ color: 'rgba(0,0,0,.65)' }}
          key={`${workTeam.id}-${index}`}
          onClick={() => {
            this.selectedValueAfterSearch(workTeam, index, 'team');
          }}
        >
          {this.renderColorText(workTeam.workTeamName)}
        </div>
      );
    });
  };

  //渲染文字
  renderColorText = text => {
    const value = this.state.searchValue;
    text = text.replace(new RegExp(value, 'g'), `<*>${value}<*>`);
    let temp = text.split('<*>');
    return temp.map((item, index) => {
      return (
        <span
          key={`${text}-${item}-${index}`}
          style={item == value ? { color: 'blue' } : { color: 'rgba(0,0,0,.65)' }}
        >
          {item}
        </span>
      );
    });
  };

  //选中的数据 [控制样式，获取数据]
  selectedValueAfterSearch = (item, index, target) => {
    if (item.active) return; //重复点击同一条数据，以下通通不执行可防止接口的重复调用
    const { userData, teamData } = this.state;
    teamData.forEach(team => {
      team.active = false;
    });
    userData.forEach(user => {
      user.active = false;
    });
    switch (target) {
      case 'user':
        userData[index].active = true;
        this.setState({ userData, teamData });
        break;
      case 'team':
        teamData[index].active = true;
        this.setState({ userData, teamData });
        break;
      default:
        return;
    }
    this.props.transferSelectedValue(item, target);
  };

  render() {
    const { userData, teamData } = this.state;
    return (
      <div className="search-user-team-tree">
        <div className="user-part">
          <div className="user-part-title">{this.$t('acp.employee')}</div>
          <div className="user-list">{this.renderUserList(userData)}</div>
        </div>
        <div className="work-team-part">
          <div className="work-team-part-title">{this.$t('workbench.workTeam.team')}</div>
          <div className="work-team-list">{this.renderTeamList(teamData)}</div>
        </div>
      </div>
    );
  }
}

export default SearchListTree;
