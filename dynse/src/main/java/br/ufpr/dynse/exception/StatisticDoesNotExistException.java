/*    
*    StatisticDoesNotExistException.java 
*    Copyright (C) 2017 Universidade Federal do Paraná, Curitiba, Paraná, Brasil
*    @Author Paulo Ricardo Lisboa de Almeida (prlalmeida@inf.ufpr.br)
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*    
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*    
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package br.ufpr.dynse.exception;

public class StatisticDoesNotExistException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private final String statisticName;

	public StatisticDoesNotExistException(String statisticName) {
		super();
		this.statisticName = statisticName;
	}

	public StatisticDoesNotExistException(String statisticName, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.statisticName = statisticName;
	}

	public StatisticDoesNotExistException(String statisticName, String message, Throwable cause) {
		super(message, cause);
		this.statisticName = statisticName;
	}

	public StatisticDoesNotExistException(String statisticName, String message) {
		super(message);
		this.statisticName = statisticName;
	}

	public StatisticDoesNotExistException(String statisticName, Throwable cause) {
		super(cause);
		this.statisticName = statisticName;
	}

	public String getStatisticName() {
		return statisticName;
	}
}
