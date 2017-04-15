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
